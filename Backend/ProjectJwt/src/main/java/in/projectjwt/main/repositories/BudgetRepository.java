package in.projectjwt.main.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.projectjwt.main.entities.Budget;



@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
}
