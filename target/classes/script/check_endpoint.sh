echo -e "Cargo CARGO_01 from SEA_PORT_Z to CITY_B Weight: 500\n"

curl -X POST -H "Content-Type: application/json" \
  -d '{
        "code": "CARGO_01",
        "weight": 500,
        "origin": "SEA_PORT_Z",
        "destination": "CITY_B"
      }' \
  http://localhost:8080/cargos

echo -e "Cargo CARGO_01 from CITY_B to WAREHOUSE_A Weight: 800 (Expected: 400 BAD REQUEST - Duplicate cargo code)\n"

curl -X POST -H "Content-Type: application/json" \
  -d '{
        "code": "CARGO_01",
        "weight": 800,
        "origin": "CITY_B",
        "destination": "WAREHOUSE_A"
      }' \
  http://localhost:8080/cargos

echo -e "\nCargo CARGO_02 from CITY_B to WAREHOUSE_A Weight: 80 (Expected: 400 BAD REQUEST - Validation failed)\n"

curl -X POST -H "Content-Type: application/json" \
  -d '{
        "code": "CARGO_02",
        "weight": 80,
        "origin": "CITY_B",
        "destination": "WAREHOUSE_A"
      }' \
  http://localhost:8080/cargos

echo -e "\nCargo CARGO_03 from SEA_PORT_Z to CITY_B Weight: 1200\n"

curl -X POST -H "Content-Type: application/json" \
  -d '{
        "code": "CARGO_03",
        "weight": 1200,
        "origin": "SEA_PORT_Z",
        "destination": "CITY_B"
      }' \
  http://localhost:8080/cargos

echo -e "\nAdd CARGO_01 to shipment 3  (400 BAD REQUEST: Cargo at wrong location)\n"

curl -X PUT http://localhost:8080/shipments/3/cargo/CARGO_01

echo -e "\nAdd CARGO_01 to shipment 1\n"

curl -X PUT http://localhost:8080/shipments/1/cargo/CARGO_01

echo -e "\nAdd CARGO_03 to shipment 1 (400 BAD REQUEST: Shipment capacity exceeded)\n"

curl -X PUT http://localhost:8080/shipments/1/cargo/CARGO_03

echo -e "\nAdd CARGO_03 to shipment 2\n"

curl -X PUT http://localhost:8080/shipments/2/cargo/CARGO_03

echo -e "\nShipment 1 arrived\n"

curl -X PUT http://localhost:8080/shipments/1/arrive

echo -e "\nStatistics\n"

curl -X GET http://localhost:8080/cargos/statistics

echo -e "\nShipment 2 arrived\n"

curl -X PUT http://localhost:8080/shipments/2/arrive

echo -e "\nStatistics\n"
curl -X GET http://localhost:8080/cargos/statistics

echo -e "\nAdd CARGO_01 en CARGO_03 to shipment 3\n"

curl -X PUT http://localhost:8080/shipments/3/cargo/CARGO_01
curl -X PUT http://localhost:8080/shipments/3/cargo/CARGO_03

echo -e "\nShipment 3 arrived\n"

curl -X PUT http://localhost:8080/shipments/3/arrive

echo -e "\nStatistics\n"
curl -X GET http://localhost:8080/cargos/statistics


echo -e "\n\nPress button to close...\n"
read close
