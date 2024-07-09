import random
import sys

#python generate_city_temperatures.py 1000


# List of 1000 different city names
cities = [
    "New York", "Los Angeles", "Chicago", "Houston", "Phoenix",
    "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose",
    "Austin", "Jacksonville", "San Francisco", "Indianapolis", "Columbus",
    "Fort Worth", "Charlotte", "Seattle", "Denver", "El Paso","Hanoi","Hamburg",
    "Berlin","Vienna","Warsaw","Budapest","Barcelona","Lisbon","Athens","Belgrade",
    "Brussels","Stockholm","Oslo","Copenhagen","Helsinki","Amsterdam","Dublin","Manchester",
    "Glasgow","Edinburgh","Birmingham","Liverpool","Sheffield","Leeds","Bristol","Cardiff","Belfast",
    "Newcastle upon Tyne","Leicester","Southampton","Portsmouth","Aberdeen","Plymouth","Brighton","Tokyo",
    "São Paulo","Québec City","Praha","München","İstanbul","Kyōto","Αθήνα","Strasbourg",
    "Nantes","Montpellier","Bordeaux","Lyon","Toulouse","Marseille","Nice","Lille","Rennes","Panama City"]

# Function to generate a random temperature with 2 decimal places
def generate_temperature():
    return round(random.uniform(-20.0, 50.0), 2)

# Function to generate city temperature data
def generate_city_temperatures(num_readings, output_file):
    print(f"Generating {num_readings} readings in {output_file}...")
    with open(output_file, 'w') as file:
        for _ in range(num_readings):
            city = random.choice(cities)
            temperature = generate_temperature()
            file.write(f"{city},{temperature}\n")
    print("Generation complete.")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python generate_city_temperatures.py <num_readings>")
        sys.exit(1)

    num_readings = int(sys.argv[1])
    output_file = "city_temperatures.txt"

    generate_city_temperatures(num_readings, output_file)
