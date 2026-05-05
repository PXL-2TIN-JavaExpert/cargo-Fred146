package be.pxl.cargo.api;

import be.pxl.cargo.api.request.CreateCargoRequest;
import be.pxl.cargo.api.response.CargoStatistics;
import be.pxl.cargo.domain.Location;
import be.pxl.cargo.service.CargoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

import static org.mockito.Mockito.when;

@WebMvcTest(CargoController.class)
public class CargoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CargoService cargoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void addCargo_validRequest_returns201() throws Exception {
        // Arrange
        CreateCargoRequest newCreateCargoRequest = new CreateCargoRequest(
                "CARGO_01",
                500,
                Location.CITY_B,
                Location.AIRPORT_X
        );

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/cargos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCreateCargoRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void addCargo_invalidRequest_returns400() throws Exception {
        // Arrange
        CreateCargoRequest newCreateCargoRequest = new CreateCargoRequest(
                "CARGO_01",
                -1,
                Location.CITY_B,
                Location.AIRPORT_X
        );

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/cargos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCreateCargoRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void getCargoStatistics_noCargos_returnsZeroedBody() throws Exception {
        // Arrange
        CargoStatistics stats = new CargoStatistics();
        stats.setStatusCount(Map.of());
        stats.setHeaviestCargo(null);
        stats.setAverageCargoWeight(0.0);
        stats.setCountCargosAtWarehouseA(0L);
        stats.setTotalWeightDeliveredAtCityB(0.0);

        when(cargoService.getCargoStatistics()).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/cargos/statistics"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.heaviestCargo").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.averageCargoWeight").value(0.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.countCargosAtWarehouseA").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalWeightDeliveredAtCityB").value(0.0));
    }
}
