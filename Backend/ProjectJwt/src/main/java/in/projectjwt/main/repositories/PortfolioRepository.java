package in.projectjwt.main.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.projectjwt.main.entities.Portfolio;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

	List<Portfolio> findByUserId(Long userId);

	List<Portfolio> findByPortfolioId(Long portfolioId);

	
}