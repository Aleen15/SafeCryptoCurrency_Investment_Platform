package in.projectjwt.main.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.projectjwt.main.entities.Expense;
import in.projectjwt.main.repositories.BudgetRepository;
import in.projectjwt.main.repositories.ExpenseRepository;



@Service
public class ExpenseService {

	@Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private  BudgetRepository budgetRepository;

    public ExpenseService(ExpenseRepository expenseRepository, BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
    }

    /**
     * Retrieve an expense by ID.
     *
     * @param expenseId The expense ID.
     * @return The expense if found.
     * @throws RuntimeException if the expense is not found.
     */
    public Expense getExpenseById(Long expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + expenseId));
    }

    /**
     * Delete an expense by ID.
     *
     * @param expenseId The expense ID.
     */
    public void deleteExpense(Long expenseId) {
        if (!expenseRepository.existsById(expenseId)) {
            throw new RuntimeException("Expense not found with ID: " + expenseId);
        }
        expenseRepository.deleteById(expenseId);
    }

	public Expense createExpense(Expense expense) {
		// TODO Auto-generated method stub
		return expenseRepository.save(expense);
		
	}
}