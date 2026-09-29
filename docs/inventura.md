# Inventura

Unos komada. Svaka šifra je jedan redak. Nema količine, pojaseva, oloa, maski ni dišalica.

## Prozor

Čipovi vrste gore, bez Sve: Odijelo, Čizmice, Peraje, Kompenzator, Rukavice, Regulator. Odabrana vrsta ostaje.

Pejzaž: lista desno. Portret: lista ispod unosa. Svi retci ostaju. Vide se zadnjih 5, najnoviji gore. Čip ne filtrira. Redak: šifra, veličina iz šifre, stanje. Ikona na retku miče taj redak.

Ispod toga, ili lijevo na pejzažu: stanje, pa za Regulator tip regulatora (Apeks `RA`, Mares `RM`, Scubapro `RS`), pa veličina samo za Peraje, Kompenzator i Rukavice, pa šifra s tipkovnicom kao na Izdavanju, pa **Unesi**. Prefiks tipa ulazi u šifru.

## Unesi

Šifra prazna: gumb šuti. Slovne vrste traže veličinu. Regulator traži tip. Ista vrsta i ista šifra drugi put ne ulaze. `X` samo je pun unos. X u šifri zamjenjuje znamenku koja se ne čita. Veličina na retku je — ako se iz šifre ne čita.

Stanje: Novo, Dobro, Za otpis, Neispravno. Jedno je označeno. Kreće na Dobro.

**Unesi** doda redak i počisti šifru, veličinu i stanje. Vrsta ostaje. Stanje se vrati na Dobro.

Popis je dummy u memoriji. Nestane kad se aplikacija zatvori.
