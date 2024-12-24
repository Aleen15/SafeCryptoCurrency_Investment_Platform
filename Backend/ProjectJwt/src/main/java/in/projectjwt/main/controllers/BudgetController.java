package in.projectjwt.main.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.projectjwt.main.entities.Budget;
import in.projectjwt.main.entities.Expense;
import in.projectjwt.main.services.BudgetService;
import in.projectjwt.main.services.ExpenseService;


@RestController
@RequestMapping("/auth/budgets")
public class BudgetController {

	@Autowired
    private BudgetService budgetService;

    @Autowired
    private ExpenseService expenseService;
    


    // Create Budget
    @PostMapping("/create-budget")
    public ResponseEntity<Budget> createBudget(@RequestBody Budget budget) {
        Budget createdBudget = budgetService.createBudget(budget);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBudget);
    }

    // Get All Budgets
    @GetMapping("/get-all-budgets")
    public ResponseEntity<List<Budget>> getAllBudgets() {
        List<Budget> budgets = budgetService.getAllBudgets();
        return ResponseEntity.ok(budgets);
    }
    
 // Add a new expense to a budget
    @PostMapping("add-expense/{budgetId}/expenses")
    public ResponseEntity<?> addExpenseToBudget(@PathVariable Long budgetId, @RequestBody Expense expense) {
        Budget budget = budgetService.getBudgetById(budgetId);

        if (budget == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Budget not found.");
        }

        double totalExpenses = budget.getExpenses() + expense.getAmount();
        if (totalExpenses > budget.getAmount()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Out of Budget: Total expenses exceed the budget limit of ₹" + budget.getAmount());
        }

        // Add the expense and update the budget's total expenses
        expense.setBudget(budget);
        expenseService.createExpense(expense);

        budget.setExpenses(totalExpenses);
        budgetService.updateBudget(budget);

        return ResponseEntity.status(HttpStatus.CREATED).body(expense);
    }
    
    
    

    // Get Expenses for a Budget
    @GetMapping("/get-expense/{budgetId}/expenses")
    public ResponseEntity<List<Expense>> getExpenses(@PathVariable Long budgetId) {
        List<Expense> expenses = budgetService.getExpensesByBudgetId(budgetId);
        return ResponseEntity.ok(expenses);
    }

    // Delete a Budget
    @DeleteMapping("/delete-budget/{budgetId}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long budgetId) {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }
    
    // Get a specific budget by ID
    @GetMapping("/get-budget/{budgetId}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long budgetId) {
        Budget budget = budgetService.getBudgetById(budgetId);
        if (budget == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(budget);
    }
}

