package in.projectjwt.main.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import in.projectjwt.main.entities.Budget;
import in.projectjwt.main.entities.Expense;
import in.projectjwt.main.repositories.BudgetRepository;
import in.projectjwt.main.repositories.ExpenseRepository;

import java.util.List;
import java.util.Optional;

@SpringBootTest
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private BudgetService budgetService;

    private Budget budget;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        budget = new Budget();
        budget.setId(1L);
        budget.setAmount(1000);
        budget.setExpenses(0);
    }

    @Test
    void testCreateBudget() {
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        Budget createdBudget = budgetService.createBudget(budget);

        assertNotNull(createdBudget);
        assertEquals(0, createdBudget.getExpenses());
        verify(budgetRepository, times(1)).save(budget);
    }

    @Test
    void testGetAllBudgets() {
        when(budgetRepository.findAll()).thenReturn(List.of(budget));

        List<Budget> budgets = budgetService.getAllBudgets();

        assertNotNull(budgets);
        assertEquals(1, budgets.size());
        verify(budgetRepository, times(1)).findAll();
    }

    @Test
    void testGetBudgetById() {
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));

        Budget foundBudget = budgetService.getBudgetById(1L);

        assertNotNull(foundBudget);
        assertEquals(1L, foundBudget.getId());
        verify(budgetRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteBudget() {
        when(budgetRepository.existsById(1L)).thenReturn(true);

        budgetService.deleteBudget(1L);

        verify(budgetRepository, times(1)).deleteById(1L);
    }

    @Test
    void testAddExpense() {
        Expense expense = new Expense();
        expense.setAmount(200);
        when(budgetRepository.findById(1L)).thenReturn(Optional.of(budget));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        Expense addedExpense = budgetService.addExpense(1L, expense);

        assertNotNull(addedExpense);
        assertEquals(200, budget.getExpenses());
        verify(expenseRepository, times(1)).save(expense);
        verify(budgetRepository, times(1)).save(budget);
    }
}
