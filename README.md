# provider-mc

## Configurazione

Impostare le seguenti variabili di ambiente:

<Root_di_contesto>_SMEUP_USER

<Root_di_contesto>_SMEUP_PASSWORD

<Root_di_contesto>_SMEUP_SERVER

<Root_di_contesto>_SMEUP_SECRET

Se la root di contesto è vuota le varibiabili diventano:

SMEUP_USER

SMEUP_PASSWORD

SMEUP_SERVER

SMEUP_SECRET

### Optional Configuration

La durata del token è impostata di default a 30 minuti.
È possibile configurarla con la variable 

<Root_di_contesto>_SMEUP_TOKEN_DURATION

## N.B.

Se la root di contesto contiene il carattere "-", l'applicazione cercherà la corrispondente 
variabile di ambiente ma con il carattere "_".
Questo per rispettare i vincoli imposti ai nomi delle variabili di ambiente

## Test

### Esempio di login

```
time curl -v -s -X POST -d 'usr=XXX&pwd=YYY4&env=ZZZ' http://localhost:8080/provider-mc/api/AuthenticateService > /dev/null
```
L'applicazione restituirà nel header "Authorization" il token nella forma:
Bearer <JWT_TOKEN>

### Esempio di fun

```
time curl -X POST -H "Authorization: Bearer <JWT_TOKEN>" -d 'fun=F(EXD;B£SER_46;WRK.SXM) 1(MB;SCP_SET;WETEST_EXD) 2(;;CHGPWD)' http://localhost:8080/provider-mc/api/fun
```
### Esempio di disconnessione

```
time curl -X POST -H "Authorization: Bearer <JWT_TOKEN>"  http://localhost:8080/api/provider-mc/DisconnectService
```