package com.itscadmo.centro_fitness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CentroFitnessApplication {

	public static void main(String[] args) {
		SpringApplication.run(CentroFitnessApplication.class, args);
	}

}
// Progetto Spring Boot: Sistema di Gestione
// Centro Fitness

// Descrizione del Progetto
// Sviluppare un'applicazione REST API per la gestione di un centro fitness che
// permetta di amministrare clienti,
// istruttori, abbonamenti e corsi della palestra.
// Obiettivi del Sistema
// Il sistema deve permettere di:
// 1. Gestione Utenti
// ● Registrare clienti e istruttori con le loro informazioni base
// ● I clienti hanno un numero tessera e un obiettivo fitness
// ● Gli istruttori hanno una specializzazione
// ● Visualizzare liste di clienti e istruttori
// 2. Gestione Abbonamenti
// ● Definire diversi tipi di abbonamenti (mensile, trimestrale, annuale) con
// prezzi diversi
// ● Far sottoscrivere un abbonamento a un cliente
// ● Visualizzare le sottoscrizioni attive
// ● Calcolare automaticamente la data di scadenza in base alla durata
// dell'abbonamento
// 3. Organizzazione Corsi
// ● Creare corsi (es. Yoga, Spinning, Pilates)
// ● Assegnare ogni corso a una sala specifica
// ● Programmare lezioni per ogni corso indicando data, ora e istruttore
// ● Visualizzare il calendario delle lezioni
// 4. Gestione Sale
// ● Registrare le diverse sale della palestra
// ● Ogni sala ha una capacità massima di persone
// ● Visualizzare quali corsi si tengono in ogni sala
// Requisiti Funzionali Principali
// Regole di Business da Implementare
// Sottoscrizione Abbonamento:
// ● Quando un cliente sottoscrive un abbonamento, il sistema deve calcolare
// automaticamente la data di
// scadenza
// ● Esempio: se sottoscrivo un abbonamento trimestrale il 1° gennaio, la
// scadenza sarà il 1° aprile

// ● Una sottoscrizione può essere ATTIVA o SCADUTA
// Programmazione Lezioni:
// ● Ogni lezione deve essere collegata a un corso
// ● Ogni lezione deve avere uno o più istruttori assegnati
// ● Ogni lezione si tiene in una sala specifica
// ● Il numero di partecipanti non può superare la capacità della sala
// ● Una lezione può essere PROGRAMMATA, COMPLETATA o CANCELLATA
// Visualizzazioni:
// ● Vedere tutte le lezioni di un determinato corso
// ● Vedere tutte le lezioni tenute da un istruttore
// ● Vedere le sottoscrizioni di un cliente
// ● Vedere tutti i corsi che si tengono in una sala