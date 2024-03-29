package controller;

import java.util.ArrayList;
import java.util.List;
import factory.unit.*;
import model.items.IEquipableItem;
import model.map.Field;
import model.map.Location;
import model.units.Alpaca;
import model.units.IUnit;
import java.util.Random;
import factory.*;

/**
 * Controller of the game.
 * The controller manages all the input received from de game's GUI.
 *
 * @author Ignacio Slater Muñoz
 * @version 2.0
 * @since 2.0
 */
public class GameController {

  private int numberOfPlayers;
  private int mapSize;
  private List<Tactician> listOfPlayers;
  private int actualRound;
  private List<Tactician> listOfWinners;
  private int maxRounds;
  private Tactician actualPlayer;
  private Field gameMap;
  private Random random;
  private int maxNumberOfPlayers;
  private MapFactory mapFactory;
  private IUnit actualUnit;


  /**
   * Creates the controller for a new game.
   *
   * @param numberOfPlayers the number of players for this game
   * @param mapSize         the dimensions of the map, for simplicity, all maps are squares
   */
  public GameController(int numberOfPlayers, int mapSize) {

    this.numberOfPlayers = numberOfPlayers;
    this.mapSize = mapSize;
    this.actualRound = 1;
    this.maxRounds = -1;
    this.random = new Random();
    this.maxNumberOfPlayers = numberOfPlayers;
    this.mapFactory = new MapFactory();
    this.gameMap = mapFactory.createMap(mapSize);

  }

  /**
   * Crea la lista inicial de jugadores
   *
   * @param maxPlayers Cantidad maxima de jugadores de la partida
   * @return Lista con todos los jugadores
   */

  public List<Tactician> addPlayers(int maxPlayers) {

    List players = new ArrayList();
    for (int i = 0; i < maxPlayers; i++) {

      Tactician player = new Tactician("Player " + i, this);
      players.add(player);
    }
    return players;
  }

  /**
   * Reordena una lista de manera aleatoria, asegurando que el el ultimo jugador nunca
   * quede al inicio de la nueva lista
   *
   * @param numberPlayers Cantidad de jugadores actuales
   * @param players       Lista de jugadores a cambiar
   * @param seed          Semilla (para cosas de testeo)
   * @return Lista de jugadores
   */

  public List<Tactician> randomList(int numberPlayers, List<Tactician> players, Random seed) {

    List<Tactician> randomPlayers = new ArrayList<Tactician>();
    List<Tactician> jugadores = players;
    Tactician ultimoJugador = jugadores.get(jugadores.size() - 1);
    jugadores.remove(jugadores.size() - 1);

    for (int i = 0; i < numberPlayers - 1; i++) {

      int numeroRandom = seed.nextInt(numberPlayers - i - 1);
      randomPlayers.add(jugadores.get(numeroRandom));
      jugadores.remove(jugadores.get(numeroRandom));
    }

    int numRandom = seed.nextInt(numberPlayers);
    randomPlayers.add(numRandom, ultimoJugador);
    return randomPlayers;
  }

  /**
   * @return the list of all the tacticians participating in the game.
   */

  public List<Tactician> getTacticians() {
    return this.listOfPlayers;
  }

  /**
   * @return the map of the current game
   */
  public Field getGameMap() {
    return this.gameMap;
  }

  /**
   * @return the tactician that's currently playing
   */
  public Tactician getTurnOwner() {
    return actualPlayer;
  }

  /**
   * @return the number of rounds since the start of the game.
   */
  public int getRoundNumber() {
    return this.actualRound;
  }

  /**
   * @return the maximum number of rounds a match can last
   */
  public int getMaxRounds() {
    return this.maxRounds;
  }

  public Tactician getActualPlayer() {
    return this.actualPlayer;
  }

  public void setActualPlayer(Tactician player) {

    this.actualPlayer = player;
  }

  /**
   * Finishes the current player's turn.
   */
  public void endTurn() {

    List<Tactician> list = this.listOfPlayers;
    int tamano = list.size();
    this.resetMovement(actualPlayer);
    if (this.getTacticians().size() == 1) {

      this.listOfWinners = this.getTacticians();
    } else if (list.get(tamano - 1).getName() == this.actualPlayer.getName()) {

      endRound();
    } else {

      int i = list.indexOf(this.actualPlayer);
      actualPlayer = list.get(i + 1);
    }
  }

  /**
   * Termina una ronda cuando todas los Tactician han jugado su turno
   */

  public void endRound() {

    if (this.getTacticians().size() == 1) {

      this.listOfWinners = this.getTacticians();
    } else if (this.getRoundNumber() == this.getMaxRounds()) {

      this.listOfWinners = this.listOfPlayers;
    } else {

      this.actualRound++;
      this.listOfPlayers = randomList(this.numberOfPlayers, this.listOfPlayers, random);
      actualPlayer = listOfPlayers.get(0);
    }
  }

  /**
   * Removes a tactician and all of it's units from the game.
   *
   * @param tactician the player to be removed
   */
  public void removeTactician(String tactician) {

    for (int i = 0; i < this.getTacticians().size(); i++) {

      if (this.getTacticians().get(i).getName().equals(tactician)) {

        if ((this.getTacticians().size() >= 2) && (this.actualPlayer == this.getTacticians().get(i))) {
          removeIUnit(actualPlayer);
          this.actualPlayer = this.getTacticians().get(i + 1);
        }

        this.listOfPlayers.remove(i);
        this.numberOfPlayers--;

      }
      if (this.getTacticians().size() == 1) {

        this.listOfWinners = this.getTacticians();
      }
    }
  }
  /**
   * Starts the game.
   *
   * @param maxTurns the maximum number of turns the game can last
   */
  public void initGame(final int maxTurns) {

    this.listOfWinners = null;
    this.listOfPlayers = randomList(maxNumberOfPlayers, addPlayers(numberOfPlayers), random);
    this.maxRounds = maxTurns;
    this.actualRound = 1;
    this.actualPlayer = this.listOfPlayers.get(0);

    for (int i = 0; i < listOfPlayers.size(); i++) {

      Tactician player = listOfPlayers.get(i);
      List<IUnit> list = setUnits(player);
      player.setUnits(list);

    }
  }

  /**
   * Starts a game without a limit of turns.
   */
  public void initEndlessGame() {

    this.listOfWinners = null;
    this.listOfPlayers = randomList(maxNumberOfPlayers, addPlayers(maxNumberOfPlayers), random);
    this.actualRound = 1;
    this.actualPlayer = this.listOfPlayers.get(0);
    this.maxRounds = -1;

    for (int i = 0; i < listOfPlayers.size(); i++) {

      Tactician player = listOfPlayers.get(i);
      List<IUnit> list = setUnits(player);
      player.setUnits(list);
    }
  }

  /**
   * @return the winner of this game, if the match ends in a draw returns a list of all the winners
   */
  public List<Tactician> getWinners() {
    return this.listOfWinners;
  }

  /**
   * @return the current player's selected unit
   */
  public IUnit getSelectedUnit() {
    return this.actualPlayer.getActualUnit();
  }


  /**
   * Selects a unit in the game map
   *
   * @param x horizontal position of the unit
   * @param y vertical position of the unit
   */
  public void selectUnitIn(int x, int y) {

    Location ubicacion = this.gameMap.getCell(x, y);
    IUnit unidadSeleccionada = ubicacion.getUnit();
    this.actualUnit = unidadSeleccionada;
  }

  /**
   * @return the inventory of the currently selected unit.
   */
  public List<IEquipableItem> getItems() {
    return this.actualPlayer.getInventoryUnit();
  }

  /**
   * Equips an item from the inventory to the currently selected unit.
   *
   * @param index the location of the item in the inventory.
   */
  public void equipItem(int index) {

    if (this.actualPlayer.getInventoryUnit().get(index) != null) {

      IEquipableItem item = this.actualPlayer.getInventoryUnit().get(index);
      this.actualPlayer.setItem(item);
    }
  }

  /**
   * Uses the equipped item on a target
   *
   * @param x horizontal position of the target
   * @param y vertical position of the target
   */
  public void useItemOn(int x, int y) {

    Location locacion = this.getGameMap().getCell(x, y);
    IUnit unit = locacion.getUnit();
    this.actualPlayer.getActualUnit().attackEnemy(unit);

  }

  /**
   * Selects an item from the selected unit's inventory.
   *
   * @param index the location of the item in the inventory.
   */
  public void selectItem(int index) {

    IEquipableItem item = this.actualPlayer.getInventoryUnit().get(index);
    this.actualPlayer.setActualItem(item);
  }

  /**
   * Gives the selected item to a target unit.
   *
   * @param x horizontal position of the target
   * @param y vertical position of the target
   */
  public void giveItemTo(int x, int y) {

    Location location = gameMap.getCell(x, y);
    IUnit unidad = location.getUnit();
    IUnit actualUnit = actualPlayer.getActualUnit();
    actualUnit.giveAway(unidad, actualPlayer.getActualItem());

  }

  /**
   * Entrega la cantidad de jugadores actuales en el juego
   * @return numero de jugadores
   */

  public int getNumberOfPlayers() {

    return this.numberOfPlayers;
  }

  /**
   * Coloca las unidades principales a un jugador
   * @param player jugador a quien se le entregaran las unidades
   * @return Lista de unidades
   */

  public List<IUnit> setUnits(Tactician player) {

    List<IUnit> list = new ArrayList<>();

    list.add(getAlpaca(player));
    list.add(getArcher(player));
    list.add(getCleric(player));
    list.add(getFighter(player));
    list.add(getHero(player));
    list.add(getSorcerer(player));
    list.add(getSwordMaster(player));

    return list;

  }

  /**
   * @return La cantidad maxima de jugadores
   */

  public int getMaxNumberOfPlayers() {

    return this.maxNumberOfPlayers;
  }

  /**
   * @return La actual unidad en juego
   */

  public IUnit getActualUnit() {

    return this.actualUnit;
  }

  /**
   * Modifica a la actual unidad en juego
   * @param unit unidad que se seteara
   */

  public void setActualUnit(IUnit unit){

    if(this.actualPlayer.getPlayerUnits().contains(unit)){

      this.actualUnit = unit;
    }
  }

  /**
   * Resetea la capacidad de movimiento de todas las unidades de un jugador
   * @param player Jugador a quien se le reseteara la capacidad de movimiento de sus unidades
   */

  public void resetMovement(Tactician player){

    for (int i = 0; i < player.getPlayerUnits().size(); i++){

      IUnit unidad = player.getPlayerUnits().get(i);
      unidad.setMove(false);

    }
  }

  /**
   * @param player Jugador que recibira la unidad
   * @return una Alpaca para el jugador
   */

  public IUnit getAlpaca(Tactician player){

    IUnit alpaca = new AlpacaFactory().createDefault(player);
    return alpaca;
  }

  /**
   * @param player Jugador que recibira la unidad
   * @return un Archer para el jugador
   */

  public IUnit getArcher(Tactician player){

    IUnit archer = new ArcherFactory().createDefault(player);
    return archer;
  }

  /**
   * @param player Jugador que recibira la unidad
   * @return un Cleric para el jugador
   */

  public IUnit getCleric(Tactician player){

    IUnit cleric = new ClericFactory().createDefault(player);
    return cleric;
  }

  /**
   * @param player Jugador que recibira la unidad
   * @return un Fighter para el jugador
   */

  public  IUnit getFighter(Tactician player){

    IUnit fighter = new FighterFactory().createDefault(player);
    return fighter;
  }

  /**
   * @param player Jugador que recibira la unidad
   * @return un Hero para el jugador
   */

  public IUnit getHero(Tactician player){

    IUnit hero = new HeroFactory().createDefault(player);
    return hero;
  }

  /**
   * @param player Jugador que recibira la unidad
   * @return un Sorcerer para el jugador
   */

  public IUnit getSorcerer(Tactician player){

    IUnit sorcerer = new SorcererFactory().createDefault(player);
    return sorcerer;
  }

  /**
   * @param player Jugador que recibira la unidad
   * @return un SwordMster para el jugador
   */

  public IUnit getSwordMaster(Tactician player){

    IUnit swordMaster = new SwordMasterFactory().createDefault(player);
    return swordMaster;
  }

  /**
   * Remove la unidad del mapa del juego
   * @param player jugador a quien se le remueve la unidad
   */

  public void removeIUnit(Tactician player){

    for(int i = 0; i < player.getPlayerUnits().size(); i++){

      player.getPlayerUnits().get(i).getLocation().removeUnit();
    }
  }
}


