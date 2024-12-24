package in.projectjwt.main.services;

import static org.mockito.Mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import in.projectjwt.main.entities.Expense;
import in.projectjwt.main.repositories.ExpenseRepository;
import in.projectjwt.main.repositories.BudgetRepository;

@SpringBootTest
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private Expense expense;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        expense = new Expense();
        expense.setId(1L);
        expense.setAmount(200);
    }

    @Test
    void testCreateExpense() {
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        Expense createdExpense = expenseService.createExpense(expense);

        assertNotNull(createdExpense);
        assertEquals(200, createdExpense.getAmount());
        verify(expenseRepository, times(1)).save(expense);
    }

    @Test
    void testGetExpenseById() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        Expense foundExpense = expenseService.getExpenseById(1L);

        assertNotNull(foundExpense);
        assertEquals(1L, foundExpense.getId());
        verify(expenseRepository, times(1)).findById(1L);
    }

    @Test
    void testGetExpenseById_NotFound() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            expenseService.getExpenseById(1L);
        });

        assertEquals("Expense not found with ID: 1", exception.getMessage());
    }

    @Test
    void testDeleteExpense() {
        when(expenseRepository.existsById(1L)).thenReturn(true);

        expenseService.deleteExpense(1L);

        verify(expenseRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteExpense_NotFound() {
        when(expenseRepository.existsById(1L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            expenseService.deleteExpense(1L);
        });

        assertEquals("Expense not found with ID: 1", exception.getMessage());
    }
}
