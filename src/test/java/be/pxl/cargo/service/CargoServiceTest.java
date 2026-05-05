package be.pxl.cargo.service;

import be.pxl.cargo.api.request.CreateCargoRequest;
import be.pxl.cargo.api.response.CargoStatistics;
import be.pxl.cargo.domain.Cargo;
import be.pxl.cargo.domain.CargoStatus;
import be.pxl.cargo.domain.Location;
import be.pxl.cargo.exceptions.NonUniqueCodeException;
import be.pxl.cargo.repository.CargoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CargoServiceTest {
    @Mock
    private CargoRepository cargoRepository;

    @InjectMocks
    private CargoService cargoService;

    @Captor
    private ArgumentCaptor<Cargo> cargoArgumentCaptorCaptor;

    @Test
    public void testCreateCargo_NewCode_CargoIsSaved() {
        CreateCargoRequest request = new CreateCargoRequest(
                "CARGO_01",
                500,
                Location.SEA_PORT_Z,
                Location.CITY_B
        );

        cargoService.createCargo(request);

        verify(cargoRepository).save(cargoArgumentCaptorCaptor.capture());
        Cargo opgeslagenCargo = cargoArgumentCaptorCaptor.getValue();

        assertEquals("CARGO_01", opgeslagenCargo.getCode());
        assertEquals(500, opgeslagenCargo.getWeight());
        assertEquals(Location.SEA_PORT_Z, opgeslagenCargo.getOrigin());
        assertEquals(Location.CITY_B, opgeslagenCargo.getDestination());

        assertEquals(CargoStatus.CREATED, opgeslagenCargo.getCargoStatus());
        assertEquals(Location.SEA_PORT_Z, opgeslagenCargo.getCurrentLocation());
    }

    @Test
    public void testCreateCargo_DuplicateCode_ThrowsNonUniqueCodeException() {
        CreateCargoRequest request = new CreateCargoRequest(
                "CARGO_01",
                500,
                Location.SEA_PORT_Z,
                Location.CITY_B
        );

        Cargo bestaandeCargo = new Cargo(
                "CARGO_01",
                500,
                Location.SEA_PORT_Z,
                Location.CITY_B
        );

        when(cargoRepository.findCargoByCode("CARGO_01"))
                .thenReturn(Optional.of(bestaandeCargo));

        assertThrows(NonUniqueCodeException.class, () -> cargoService.createCargo(request));

        verify(cargoRepository, never()).save(any());
    }

    @Test
    public void testGetCargoStatistics_ReturnsCorrectValues() {
        Cargo cargo1 = new Cargo("CARGO_01", 500, Location.SEA_PORT_Z, Location.WAREHOUSE_A);
        cargo1.arrive(Location.WAREHOUSE_A);

        Cargo cargo2 = new Cargo("CARGO_03", 1200, Location.SEA_PORT_Z, Location.CITY_B);
        cargo2.setCargoStatus(CargoStatus.MOVING);

        Cargo cargo3 = new Cargo("CARGO_04", 300, Location.SEA_PORT_Z, Location.CITY_B);
        cargo3.arrive(Location.CITY_B);

        when(cargoRepository.findAll()).thenReturn(List.of(cargo1, cargo2, cargo3));

        CargoStatistics statistics = cargoService.getCargoStatistics();

        assertNotNull(statistics);
        assertEquals("CARGO_03", statistics.getHeaviestCargo());
        assertEquals((500 + 1200 + 300) / 3.0, statistics.getAverageCargoWeight(), 0.01);
        assertEquals(1, statistics.getCountCargosAtWarehouseA());
        assertEquals(300.0, statistics.getTotalWeightDeliveredAtCityB());
        assertEquals(1L, statistics.getStatusCount().get(CargoStatus.MOVING));
        assertEquals(2L, statistics.getStatusCount().get(CargoStatus.DELIVERED));

        verify(cargoRepository).findAll();
    }
}
