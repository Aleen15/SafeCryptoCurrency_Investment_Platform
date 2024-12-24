package in.projectjwt.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import in.projectjwt.main.entities.CoinData;




public interface CoinRepository extends JpaRepository<CoinData,String> {

	

	
}

