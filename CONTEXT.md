# GE Armory

Evidencija opreme za izlete: tko ide, što je traženo, što je izdano, i što je na stanju.

## Language

**Komad**:
Jedan fizički predmet opreme, identificiran šifrom.
_Avoid_: artikal, stavka, item

**Šifra**:
Identifikator jednog komada. Nosi veličinu i broj tog komada. X zamjenjuje znamenku koja se s komada ne može pročitati.
_Avoid_: kod, ID

**Broj komada**:
Redni broj komada unutar iste veličine. Dio šifre. Primjer: u `0512` i `M12` broj je 12.
_Avoid_: šifra, količina

**Vrsta opreme**:
Tip komada. Primjer: Odijelo.
_Avoid_: kategorija, naziv

**Veličina**:
Veličina komada, ovisi o vrsti. Odijelo 1–9, čizma 3–12, peraja S, R ili XL, kompenzator XS, S, M, L, XL ili XXL, rukavice S, M, L ili XL. Regulator je nema.
_Avoid_: broj, količina

**Status**:
Gdje je komad. Na stanju ili Izdano.
_Avoid_: stanje

**Stanje**:
Kakvoća komada na inventuri odijela, peraja, čizmica, rukavica, kompenzatora i regulatora. Novo, Dobro, Za otpis ili Neispravno.
_Avoid_: status

**Izdavanje**:
Prozor na kojem se komad zadužuje članu.
_Avoid_: checkout, posudba

**Član**:
Osoba kluba. Može dobiti opremu i bez zahtjeva.
_Avoid_: sudionik, korisnik

**Zaduženje**:
Predaja komada jednom članu. Select nudi samo članove koji su zatražili opremu. Novo zaduženje može i za člana koji nije zatražio.
_Avoid_: posudba

**Razduživanje**:
Povrat komada koji član drži. Vraćeni komad je Na stanju.
_Avoid_: check-in, odjava

**Inventura**:
Unos opreme koja se broji. Neke vrste zbrajaju količinu, neke dodaju redak. Popis stoji na tabletu.
_Avoid_: lista opreme, stocktake

**Količina**:
Zbroj unešen na inventuri. Svaki Unesi dodaje na postojeći broj. Vrijedi za pojaseve, olo, maske i dišalice.
_Avoid_: veličina, šifra

**Redak inventure**:
Jedan komad na inventuri, identificiran šifrom. Odijelo, peraje, kompenzator, regulator, čizmice ili rukavice. Svaki Unesi je novi redak.
_Avoid_: količina

**Izlet**:
Izlazak koji ima sudionike i zahtjeve za opremom. Na početnoj je jedan redak.
_Avoid_: trip, tura

**Sudionik**:
Osoba na izletu. Na početnoj se vidi samo broj.
_Avoid_: korisnik

**Zahtjev za opremom**:
Tražena oprema za izlet. Na početnoj se vidi samo broj zahtjeva.
_Avoid_: narudžba, rezervacija

**Olo**:
Utezi. Svaki je 1 kg ili 2 kg.
_Avoid_: olovo, uteg
