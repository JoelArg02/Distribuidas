from locust import HttpUser, task, between
import random
from datetime import datetime, timedelta
import string
import time

class MyUser(HttpUser):
    host = "http://localhost:8080"
    wait_time = between(1, 3)
    
    @task
    def create_articulo(self):
        titulos = [
            "Realismo mágico latinoamericano",
            "Análisis de la narrativa contemporánea",
            "Estudios literarios modernos",
            "Crítica literaria del siglo XXI",
            "Tendencias en la literatura hispanoamericana",
            "Nuevas perspectivas en estudios culturales",
            "Literatura y sociedad en América Latina"
        ]
        
        editoriales = [
            "Rev. Literaria",
            "Editorial Académica",
            "Publicaciones Universitarias",
            "Editorial Científica",
            "Revista de Estudios",
            "Editorial Latinoamericana"
        ]
        
        areas = [
            "Literatura",
            "Lingüística",
            "Estudios Culturales",
            "Crítica Literaria",
            "Análisis Textual",
            "Teoría Literaria"
        ]
        
        idiomas = ["Español", "Inglés", "Portugués", "Francés"]

        rand_num = random.randint(1, 10000)
        anio = random.randint(2015, 2024)

        # Fecha aleatoria del año
        start_date = datetime(anio, 1, 1)
        end_date = datetime(anio, 12, 31)
        random_date = start_date + timedelta(days=random.randint(0, (end_date - start_date).days))

        # Unicidad garantizada
        timestamp = int(time.time() * 1000)
        random_suffix = ''.join(random.choices(string.ascii_letters + string.digits, k=4))
        revista_unica = f"Revista {random_suffix}_{timestamp}"
        doi_unico = f"10.{random.randint(1000,9999)}/{random_suffix.lower()}.{timestamp}"

        payload = {
            "titulo": f"{random.choice(titulos)} - Estudio {rand_num}",
            "anioPublicacion": anio,
            "editorial": random.choice(editoriales),
            "isbn": None,
            "resumen": f"Análisis detallado sobre {random.choice(areas).lower()} con enfoque en metodologías contemporáneas.",
            "idioma": random.choice(idiomas),
            "revista": revista_unica,
            "doi": doi_unico,
            "areaInvestigacion": random.choice(areas),
            "fechaPublicacion": random_date.strftime("%Y-%m-%d"),
            "autorId": random.randint(1, 100)
        }

        try:
            response = self.client.post("/articulos", 
                                        json=payload,
                                        headers={"Content-Type": "application/json"},
                                        timeout=10)
            
            print(f"Status: {response.status_code}")
            if response.status_code == 201:
                print(f"✓ Artículo creado: {payload['titulo']}")
            else:
                print(f"✗ Error {response.status_code}: {response.text}")
                
        except Exception as e:
            print(f"✗ Error de conexión: {str(e)}")
    
    @task
    def get_articulos(self):
        try:
            response = self.client.get("/articulos")
            print(f"GET artículos - Status: {response.status_code}")
        except Exception as e:
            print(f"Error en GET: {str(e)}")
