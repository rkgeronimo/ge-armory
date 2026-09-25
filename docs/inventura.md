# Inventura

Unos količine po vrsti. Nema šifre, osobe ni datuma.

Isti oblik kao izdavanje. Gumb je **Unesi**.

## Prozor

Naslov **Inventura**. Tabovi su vrste. U tabu su samo polja koja ta vrsta traži. **Unesi** je visok 56px: na mobitelu preko cijele širine na dnu, na tabletu u obrascu.

```
Inventura

[ Pojasevi ] [ Olo ] [ Maske ] [ Dišalice ]
[ Odijela ] [ Peraje ] [ Kompenzatori ] [ Regulatori ] [ Čizmice ]

količina    [ 12 ]

[ Unesi ]
```

Veličina se pokazuje samo ako je vrsta ima. Pojasevi, olo, maske i dišalice je nemaju.

## Količina

**Unesi** zbraja broj na postojeću količinu. Ne prepisuje je.

| Tab | Polja | Unesi |
| --- | --- | --- |
| Pojasevi | količina | broj pojaseva |
| Olo | 1 kg ili 2 kg, pa količina | broj utega te mase |
| Maske | količina | broj maski |
| Dišalice | količina | broj dišalica |

Olo: dva velika izbora, **1 kg** i **2 kg**. Jedan je označen. Količina vrijedi samo za označenu masu. Unos od 1 kg ne dira 2 kg.

## Redak

Svaki **Unesi** dodaje novi redak. Ne stapa se u jedan broj.

| Tab | Stanje |
| --- | --- |
| Odijela | da |
| Peraje | da |
| Čizmice | da |
| Rukavice | da |
| Kompenzatori | da |
| Regulatori | da |

**Stanje** je kakvoća komada: Novo, Dobro, Za otpis, Neispravno. Četiri velika izbora, jedan je označen. Na retku stoji uz ostala polja tog taba.

## Spremanje

Popis ostaje na tabletu, u aplikaciji. **Unesi** ga odmah zapiše. Nema zasebnog gumba za spremanje. Popis je tu i kad se aplikacija zatvori.
