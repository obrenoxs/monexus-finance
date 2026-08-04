package com.monexus.finance;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requer conexão real com banco de dados (sobe o contexto Spring completo, incluindo Flyway). "
		+ "Desabilitado temporariamente enquanto a V1 cobre apenas testes unitários (sem banco). "
		+ "Reabilitar quando entrarmos na fase de testes de integração com banco real.")
class MonexusFinanceApplicationTests {

	@Test
	void contextLoads() {
	}

}
