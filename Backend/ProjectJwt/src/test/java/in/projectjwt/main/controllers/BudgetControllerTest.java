package in.projectjwt.main.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.test.web.servlet.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import in.projectjwt.main.entities.Budget;
import in.projectjwt.main.entities.Expense;
import in.projectjwt.main.services.BudgetService;
import in.projectjwt.main.services.ExpenseService;

@SpringBootTest
class BudgetControllerTest {

    @MockBean
    private BudgetService budgetService;

    @MockBean
    private ExpenseService expenseService;

    @Autowired
    private MockMvc mockMvc;

    private Budget budget;
    private Expense expense;

    @BeforeEach
    void setUp() {
        budget = new Budget();
        budget.setId(1L);
        budget.setAmount(1000);
        budget.setExpenses(0);

        expense = new Expense();
        expense.setAmount(200);
    }

    @Test
    void testCreateBudget() throws Exception {
        when(budgetService.createBudget(any(Budget.class))).thenReturn(budget);

        mockMvc.perform(post("/auth/budgets/create-budget")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 1000}"))
                .andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.amount").value(1000));
    }

    @Test
    void testGetAllBudgets() throws Exception {
        when(budgetService.getAllBudgets()).thenReturn(List.of(budget));

        mockMvc.perform(get("/auth/budgets/get-all-budgets"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$[0].amount").value(1000));
    }

    @Test
    void testGetBudgetById() throws Exception {
        when(budgetService.getBudgetById(1L)).thenReturn(budget);

        mockMvc.perform(get("/auth/budgets/get-budget/1"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.amount").value(1000));
    }

    @Test
    void testDeleteBudget() throws Exception {
        doNothing().when(budgetService).deleteBudget(1L);

        mockMvc.perform(delete("/auth/budgets/delete-budget/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testAddExpenseToBudget() throws Exception {
        when(budgetService.getBudgetById(1L)).thenReturn(budget);
        when(expenseService.createExpense(any(Expense.class))).thenReturn(expense);
        when(budgetService.updateBudget(any(Budget.class))).thenReturn(budget);

        mockMvc.perform(post("/auth/budgets/add-expense/1/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 200}"))
                .andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.amount").value(200));
    }

    @Test
    void testGetExpenses() throws Exception {
        List<Expense> expenses = List.of(expense);
        when(budgetService.getExpensesByBudgetId(1L)).thenReturn(expenses);

        mockMvc.perform(get("/auth/budgets/get-expense/1/expenses"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$[0].amount").value(200));
    }
}

