from locust import HttpUser, task, between
import random
import string

class PublicacionUser(HttpUser):
    host = "http://192.168.68.106:8080"
    wait_time = between(0.5, 1.5)

    nombres = ["Isabella", "Mateo", "Sofía", "Lucas", "Valentina", "Gabriel", "Camila", "Sebastián", "María", "Andrés"]
    apellidos = ["Reyes", "Gómez", "Fernández", "Díaz", "Ramírez", "Torres", "Mendoza", "Silva", "Morales", "Castillo"]

    def generar_orcid(self):
        return f"0000-000{random.randint(0,9)}-{random.randint(1000,9999)}-{random.randint(1000,9999)}"

    def generar_email(self, nombre, apellido):
        sufijo = ''.join(random.choices(string.digits, k=4))
        return f"{nombre.lower()}.{apellido.lower()}{sufijo}@espe.edu.ec"

    def generar_telefono(self):
        return f"+5939{random.randint(10000000, 99999999)}"

    @task
    def crear_autor(self):
        nombre = random.choice(self.nombres)
        apellido = random.choice(self.apellidos)

        payload = {
            "nombre": nombre,
            "apellido": apellido,
            "email": self.generar_email(nombre, apellido),
            "telefono": self.generar_telefono(),
            "orcid": self.generar_orcid(),
            "nacionalidad": "Ecuatoriana",
            "institucion": "Universidad de las Fuerzas Armadas ESPE"
        }

        with self.client.post("/autores", json=payload, catch_response=True) as response:
            if response.status_code != 201:
                response.failure(f"Fallo al crear autor: {response.text}")
            else:
                response.success()
