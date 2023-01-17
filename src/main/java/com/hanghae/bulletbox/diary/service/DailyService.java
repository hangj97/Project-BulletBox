package com.hanghae.bulletbox.diary.service;

import com.hanghae.bulletbox.category.dto.CategoryDto;
import com.hanghae.bulletbox.category.dto.ResponseCategoryDto;
import com.hanghae.bulletbox.category.entity.Category;
import com.hanghae.bulletbox.category.repository.CategoryRepository;
import com.hanghae.bulletbox.diary.dto.DailyDto;
import com.hanghae.bulletbox.diary.dto.ResponseDailyDto;
import com.hanghae.bulletbox.diary.dto.ResponseShowDailyDto;
import com.hanghae.bulletbox.member.entity.Member;
import com.hanghae.bulletbox.member.repository.MemberRepository;
import com.hanghae.bulletbox.todo.dto.TodoDto;
import com.hanghae.bulletbox.todo.entity.TodoMemo;
import com.hanghae.bulletbox.todo.entity.Todo;
import com.hanghae.bulletbox.todo.repository.TodoMemoRepository;
import com.hanghae.bulletbox.todo.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.hanghae.bulletbox.common.exception.ExceptionMessage.NOT_FOUND_MEMBER_MSG;

@Service
@RequiredArgsConstructor
public class DailyService {

    private final MemberRepository memberRepository;

    private final CategoryRepository categoryRepository;

    private final TodoRepository todoRepository;

    private final TodoMemoRepository todoMemoRepository;

    @Transactional(readOnly = true)
    public ResponseDailyDto showDailyPage(Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException(NOT_FOUND_MEMBER_MSG.getMsg())
        );

        List<CategoryDto> categoryDtoList = new ArrayList<>();
        List<Category> categoryList = categoryRepository.findAllByMember(member);

        for (Category category : categoryList) {

            categoryDtoList.add(CategoryDto.toCategoryDto(category.getCategoryId(), category.getCategoryName(), null));
        }

        List<DailyDto> dailyDtoList = new ArrayList<>();
        Long todoYear = (long) LocalDate.now().getYear();
        Long todoMonth = (long) LocalDate.now().getMonthValue();
        Long todoDay = (long) LocalDate.now().getDayOfMonth();
        List<Todo> todoList = todoRepository.findAllByMemberAndTodoYearAndTodoMonthAndTodoDay(member, todoYear, todoMonth, todoDay);

        List<TodoMemo> todoMemoList = todoMemoRepository.findAllByMember(member);
        for (Todo todo : todoList) {
            for (TodoMemo todoMemo : todoMemoList) {
                if (todoMemo.getTodo().equals(todo)) {
                    dailyDtoList.add(DailyDto.toDailyDto(todo, todoMemoList));
                }
            }
        }

        return ResponseDailyDto.toResponseDailyDto(categoryDtoList, dailyDtoList, true);
    }

    public ResponseShowDailyDto showDailyPageChangeDay(TodoDto todoDto) {
        Long memberId = todoDto.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException(NOT_FOUND_MEMBER_MSG.getMsg())
        );
        List<DailyDto> dailyDtoList = new ArrayList<>();
        Long todoYear = todoDto.getTodoYear();
        Long todoMonth = todoDto.getTodoMonth();
        Long todoDay = todoDto.getTodoDay();
        List<Todo> todoList = todoRepository.findAllByMemberAndTodoYearAndTodoMonthAndTodoDay(member, todoYear, todoMonth, todoDay);
        List<TodoMemo> todoMemoList = todoMemoRepository.findAllByMember(member);

        for (Todo todo : todoList) {
            for (TodoMemo todoMemo : todoMemoList) {
                if (todoMemo.getTodo().equals(todo)) {
                    dailyDtoList.add(DailyDto.toDailyDto(todo, todoMemoList));
                }
            }
        }
        return ResponseShowDailyDto.toResponseShowDailyDto(dailyDtoList);
    }

    public ResponseCategoryDto showDailyByCategory(TodoDto todoDto){

        Long memberId = todoDto.getMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException(NOT_FOUND_MEMBER_MSG.getMsg())
        );

        List<DailyDto> dailyDtoList = new ArrayList<>();
        Long categoryId = todoDto.getCategoryId();
        Long todoYear = todoDto.getTodoYear();
        Long todoMonth = todoDto.getTodoMonth();
        Long todoDay = todoDto.getTodoDay();
        List<Todo> todoList = todoRepository.findAllByMemberAndCategoryIdAndTodoYearAndTodoMonthAndTodoDay(member, categoryId, todoYear, todoMonth, todoDay);

        List<TodoMemo> todoMemoList = todoMemoRepository.findAllByMember(member);
        for (Todo todo : todoList) {
            for (TodoMemo todoMemo : todoMemoList) {
                if (todoMemo.getTodo().equals(todo)) {
                    dailyDtoList.add(DailyDto.toDailyDto(todo, todoMemoList));
                }
            }
        }

        return ResponseCategoryDto.toResponseCategoryDto(dailyDtoList);
    }

}