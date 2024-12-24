package in.projectjwt.main.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.projectjwt.main.entities.Budget;
import in.projectjwt.main.entities.Expense;
import in.projectjwt.main.repositories.BudgetRepository;
import in.projectjwt.main.repositories.ExpenseRepository;


@Service
public class BudgetService {

	@Autowired
    private  BudgetRepository budgetRepository;
    
	@Autowired
    private  ExpenseRepository expenseRepository;

    public BudgetService(BudgetRepository budgetRepository, ExpenseRepository expenseRepository) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
    }

    /**
     * Create a new budget.
     *
     * @param budget The budget object to create.
     * @return The created budget.
     */
    public Budget createBudget(Budget budget) {
        budget.setExpenses(0); // Initialize expenses to 0
        return budgetRepository.save(budget);
    }

    /**
     * Retrieve all budgets.
     *
     * @return List of all budgets.
     */
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    /**
     * Retrieve a specific budget by ID.
     *
     * @param budgetId The budget ID.
     * @return The budget if found.
     * @throws RuntimeException if the budget is not found.
     */
    public Budget getBudgetById(Long budgetId) {
        return budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found with ID: " + budgetId));
    }

    /**
     * Delete a budget by ID.
     *
     * @param budgetId The budget ID.
     */
    public void deleteBudget(Long budgetId) {
        if (!budgetRepository.existsById(budgetId)) {
            throw new RuntimeException("Budget not found with ID: " + budgetId);
        }
        budgetRepository.deleteById(budgetId);
    }

    /**
     * Add an expense to a specific budget.
     *
     * @param budgetId The ID of the budget.
     * @param expense  The expense object to add.
     * @return The added expense.
     */
    public Expense addExpense(Long budgetId, Expense expense) {
        Budget budget = getBudgetById(budgetId);

        // Update the budget's expenses
        budget.setExpenses(budget.getExpenses() + expense.getAmount());

        // Set the budget in the expense and save it
        expense.setBudget(budget);

        // Save the updated budget and expense
        budgetRepository.save(budget);
        return expenseRepository.save(expense);
    }

    /**
     * Retrieve all expenses for a specific budget.
     *
     * @param budgetId The budget ID.
     * @return List of expenses.
     */
    public List<Expense> getExpensesByBudgetId(Long budgetId) {
        return expenseRepository.findByBudgetId(budgetId);
    }

	public Budget updateBudget(Budget budget) {
		// TODO Auto-generated method stub
		 return budgetRepository.save(budget);
		
	}
}