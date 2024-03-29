package controller;


import java.util.*;
import java.util.stream.IntStream;

import factory.MapFactory;
import factory.item.AxeFactory;
import factory.unit.*;
import model.items.IEquipableItem;
import model.map.Field;
import model.map.InvalidLocation;
import model.map.Location;
import model.units.IUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ignacio Slater Muñoz
 * @since v2.0
 */
class GameControllerTest {

  private GameController controller;
  private long randomSeed;
  private List<Tactician> testTacticians;
  private AxeFactory axeFactory;
  private AlpacaFactory alpacaFactory;
  private ArcherFactory archerFactory;
  private ClericFactory clericFactory;
  private FighterFactory fighterFactory;
  private HeroFactory heroFactory;
  private SorcererFactory sorcererFactory;
  private SwordMasterFactory swordMasterFactory;
  private MapFactory mapFactory;

  @BeforeEach
  void setUp() {
    randomSeed = new Random().nextLong();
    controller = new GameController(4, 4);
    controller.initEndlessGame();
    testTacticians = controller.getTacticians();
    axeFactory = new AxeFactory();
    alpacaFactory = new AlpacaFactory();
    archerFactory = new ArcherFactory();
    clericFactory = new ClericFactory();
    fighterFactory = new FighterFactory();
    heroFactory = new HeroFactory();
    sorcererFactory = new SorcererFactory();
    swordMasterFactory = new SwordMasterFactory();
    mapFactory = new MapFactory();
  }

  public boolean vecinos(Field gMap, Field map) {

    int contador = 0;
    for (int i = 0; i < 7; i++) {
      for (int j = 0; j < 7; j++) {

        Location ub1 = gMap.getCell(i, j);
        Location ub2 = map.getCell(i, j);
        Set<Location> vecinos1 = ub1.getNeighbours();
        Set<Location> vecinos2 = ub2.getNeighbours();
        Iterator<Location> itr = vecinos1.iterator();
        Iterator<Location> itr1 = vecinos2.iterator();
        while (itr.hasNext()) {
          Location a = itr.next();
          while (itr1.hasNext()) {
            Location b = itr1.next();
            if (a.equals(b)) {
              contador = 0;
              break;
            } else {
              contador++;
            }
          }
          if (contador != 0) {
            return false;
          }
          contador = 0;
        }
      }
    }
    return true;
  }

  public IUnit randomUnit() {

    Field map = controller.getGameMap();
    InvalidLocation invalid = new InvalidLocation();
    for (int i = 0; i < map.getSize(); i++) {
      for (int j = 0; j < map.getSize(); j++) {

        if (!map.getCell(i, j).equals(invalid)) {

          IUnit unit = heroFactory.create(20,  null);
          unit.setLocation(map.getCell(i, j));
          map.getCell(i, j).setUnit(unit);
          return unit;
        }
      }
    }
    return null;
  }

  public boolean isTactician(String player, List<Tactician> list) {

    for (int i = 0; i < list.size(); i++) {

      if (list.get(i).getName().equals(player)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Verifica el metodo getTacticians
   */
  @Test
  void getTacticians() {
    List<Tactician> tacticians = controller.getTacticians();
    assertEquals(4, tacticians.size());
    for (int i = 0; i < tacticians.size(); i++) {

      assertTrue(isTactician("Player " + i, tacticians));

    }
  }

  /**
   * Verifica el metodo getGameMap comparando dos mapas con la misma semilla
   */

  @Test
  void getGameMap() {
    Field gameMap = controller.getGameMap();
    assertEquals(4, gameMap.getSize());
    assertTrue(controller.getGameMap().isConnected());
    Random testRandom = gameMap.getSeed();
    Field a = gameMap;
    controller.getGameMap().getMap().clear();
    Field map = mapFactory.createMapSeed(7, testRandom, controller.getGameMap());
    assertTrue(vecinos(a, map));

  }

  @Test
  void getTurnOwner() {

    controller.initGame(4);
    List<Tactician> list = controller.getTacticians();
    for (int i = 0; i < controller.getTacticians().size(); i++) {

      Tactician player = controller.getTurnOwner();
      Tactician realPlayer = list.get(i);
      assertEquals(player, realPlayer);
      controller.endTurn();
    }


  }
  @Test
  void getRoundNumber() {
    controller.initGame(10);
    for (int i = 1; i < 10; i++) {

      assertEquals(i, controller.getRoundNumber());
      for (int j = 0; j < 4; j++) {
        controller.endTurn();
      }
    }
  }

  @Test
  void getMaxRounds() {
    Random randomTurnSequence = new Random();
    IntStream.range(0, 50).map(i -> randomTurnSequence.nextInt() & Integer.MAX_VALUE).forEach(nextInt -> {
      controller.initGame(nextInt);
      System.out.println(nextInt);
      assertEquals(nextInt, controller.getMaxRounds());
      System.out.println(nextInt);
    });
    controller.initEndlessGame();
    assertEquals(-1, controller.getMaxRounds());
  }

  @Test
  void endTurn() {
    Tactician firstPlayer = controller.getTurnOwner();
    int index = controller.getTacticians().indexOf(firstPlayer);
    Tactician secondPlayer = controller.getTacticians().get(index + 1);
    Tactician thirdPlayer = controller.getTacticians().get(index + 2);
    assertNotEquals(secondPlayer.getName(), firstPlayer.getName());

    controller.endTurn();
    assertNotEquals(firstPlayer.getName(), controller.getTurnOwner().getName());
    assertEquals(secondPlayer.getName(), controller.getTurnOwner().getName());

    controller.endTurn();
    assertNotEquals(firstPlayer.getName(), controller.getTurnOwner().getName());
    assertNotEquals(secondPlayer.getName(), controller.getTurnOwner().getName());
    assertEquals(thirdPlayer.getName(), controller.getTurnOwner().getName());
  }

  /**
   * Verifica que el Tactician sea removido de manera correcta
   */

  @Test
  void removeTactician() {
    assertEquals(4, controller.getTacticians().size());
    controller.getTacticians()
            .forEach(tactician -> Assertions.assertTrue(testTacticians.contains(tactician)));

    controller.removeTactician("Player 0");
    assertEquals(3, controller.getTacticians().size());
    controller.getTacticians().forEach(tactician -> assertNotEquals("Player 0", tactician));
    controller.getTacticians()
            .forEach(tactician -> Assertions.assertTrue(testTacticians.contains(tactician)));

    controller.removeTactician("Player 5");
    assertEquals(3, controller.getTacticians().size());
    controller.getTacticians()
            .forEach(tactician -> Assertions.assertTrue(testTacticians.contains(tactician)));

  }

  @Test
  void getWinners() {
    controller.initGame(2);
    IntStream.range(0, 8).forEach(i -> controller.endTurn());
    assertEquals(4, controller.getWinners().size());
    for (int i = 0; i < controller.getTacticians().size(); i++) {

      Tactician player = controller.getTacticians().get(i);
      assertTrue(controller.getWinners().contains(player));
    }

    controller.initGame(2);
    IntStream.range(0, 4).forEach(i -> controller.endTurn());
    assertNull(controller.getWinners());
    controller.removeTactician("Player 0");
    controller.removeTactician("Player 2");
    controller.endTurn();
    controller.endTurn();
    List<Tactician> winners = controller.getWinners();
    Tactician winner1 = winners.get(0);
    Tactician winner2 = winners.get(1);
    assertEquals(2, winners.size());
    assertTrue(List.of(winner1, winner2).containsAll(winners));

    controller.initEndlessGame();
    for (int i = 0; i < 3; i++) {
      assertNull(controller.getWinners());
      controller.removeTactician("Player " + i);
    }
    Tactician winner3 = controller.getTacticians().get(0);
    assertTrue(List.of(winner3).containsAll(controller.getWinners()));
  }

  @Test
  void getSelectedUnit() {

    List<Tactician> listaTactician = controller.getTacticians();
    this.controller.setActualPlayer(listaTactician.get(0));
    IUnit unidadValida = randomUnit();
    controller.getActualPlayer().addUnit(unidadValida);
    unidadValida.setOwner(controller.getActualPlayer());
    int x = unidadValida.getLocation().getRow();
    int y = unidadValida.getLocation().getColumn();
    controller.getActualPlayer().setActualUnit(unidadValida);
    assertEquals(unidadValida, controller.getActualPlayer().getActualUnit());
    assertEquals(unidadValida, controller.getSelectedUnit());

  }

  @Test
  void selectUnitIn() {

    List<Tactician> listaTactician = controller.getTacticians();
    this.controller.setActualPlayer(listaTactician.get(1));
    IUnit unidadValida = randomUnit();
    controller.getActualPlayer().addUnit(unidadValida);
    unidadValida.setOwner(controller.getActualPlayer());
    int x = unidadValida.getLocation().getRow();
    int y = unidadValida.getLocation().getColumn();
    controller.selectUnitIn(x, y);
    assertEquals(unidadValida, controller.getActualUnit());


  }

  @Test
  void getItems() {

    List<Tactician> listaTactician = controller.getTacticians();
    Tactician player = listaTactician.get(0);
    this.controller.setActualPlayer(player);
    IUnit unidad = randomUnit();
    IEquipableItem objeto = axeFactory.create(2, 1, 2);
    List lista = new ArrayList();
    lista.add(objeto);
    unidad.addItem(objeto);
    player.addUnit(unidad);
    this.controller.getActualPlayer().setActualUnit(unidad);
    assertEquals(lista, this.controller.getItems());

  }

  /**
   * Verifica que a la unidad se le equipe el item correcto
   */
  @Test
  void equipItem() {

    List<Tactician> listaTactician = controller.getTacticians();
    Tactician player = listaTactician.get(0);
    this.controller.setActualPlayer(player);
    IEquipableItem objeto = axeFactory.create(2, 1, 2);
    IUnit unidad = randomUnit();
    List lista = new ArrayList();
    lista.add(objeto);
    player.addUnit(unidad);
    unidad.addItem(objeto);
    this.controller.getActualPlayer().setActualUnit(unidad);
    assertEquals(this.controller.getActualPlayer().getActualUnit().getEquippedItem(), this.controller.getActualPlayer().getActualUnit().getHand());
    this.controller.equipItem(0);
    assertEquals(objeto, this.controller.getActualPlayer().getActualUnit().getEquippedItem());


  }

  /**
   * Verifica que el item de la unidad sea usado en la posicion correcta
   */

  @Test
  void useItemOn() {

    List<Tactician> listaTactician = controller.getTacticians();
    this.controller.setActualPlayer(listaTactician.get(0));
    IUnit unidadValida = randomUnit();
    int x = unidadValida.getLocation().getRow();
    int y = unidadValida.getLocation().getColumn();
    controller.selectUnitIn(x, y);
    controller.getActualPlayer().addUnit(unidadValida);
    controller.getActualPlayer().setActualUnit(unidadValida);
    controller.useItemOn(0, 0);

  }

  /**
   * Verifica que el item de la unidad sea seleccionado de manera correcta
   */
  @Test
  void selectItem() {

    List<Tactician> listaTactician = controller.getTacticians();
    Tactician player = listaTactician.get(0);
    this.controller.setActualPlayer(player);
    IEquipableItem objeto = axeFactory.create(2, 1, 2);
    IUnit unidad = randomUnit();
    unidad.addItem(objeto);
    player.addUnit(unidad);
    player.setActualUnit(unidad);
    assertNull(controller.getActualPlayer().getActualItem());
    controller.selectItem(0);
    assertEquals(objeto, controller.getActualPlayer().getActualItem());


  }

  /**
   * Verifica que el item de la unidad sea entregado a otra de manera correcta
   */
  @Test
  void giveItemTo() {

    IUnit selectUnit = heroFactory.createDefault(testTacticians.get(0));
    IUnit unit = heroFactory.createDefault(testTacticians.get(1));
    controller.getGameMap().getCell(0, 1 ).setUnit(selectUnit);
    controller.getGameMap().getCell(0, 0).setUnit(unit);
    IEquipableItem objeto = axeFactory.create(2, 1, 2);
    IEquipableItem objeto2 = axeFactory.create(3, 1, 3);
    unit.addItem(objeto2);
    assertFalse(selectUnit.getItems().contains(objeto));
    assertFalse(unit.getItems().contains(objeto));
    selectUnit.addItem(objeto);
    controller.getActualPlayer().addUnit(selectUnit);
    assertTrue(selectUnit.getItems().contains(objeto));
    assertFalse(unit.getItems().contains(objeto));
    controller.setActualPlayer(this.testTacticians.get(0));
    controller.getActualPlayer().setActualUnit(selectUnit);
    controller.getActualPlayer().setActualItem(objeto);
    controller.giveItemTo(0, 1);
    if (selectUnit.getLocation().getNeighbours().size() == 0 || unit.getLocation().getNeighbours().size() == 0) {
      assertFalse(unit.getItems().contains(objeto));
    } else {
      assertTrue(unit.getItems().contains(objeto));
    }

  }

  @Test
  void getNumberPlayers() {

    int number = controller.getTacticians().size();
    assertEquals(number, controller.getNumberOfPlayers());
    controller.removeTactician("Player 0");
    int newNumber = controller.getTacticians().size();
    assertEquals(newNumber, controller.getNumberOfPlayers());

  }

  @Test
  void getMaxNumberOfPlayers() {

    controller.initGame(4);
    assertEquals(4, controller.getNumberOfPlayers());
    controller.removeTactician("Player 0");
    assertEquals(4, controller.getMaxNumberOfPlayers());
    controller.removeTactician("Player 1");
    assertEquals(4, controller.getMaxNumberOfPlayers());
    controller.removeTactician("Player 0");
    assertEquals(4, controller.getMaxNumberOfPlayers());
  }


  /**
   * Verifica que si una unidad muere, esta sea extraida de la casilla
   */
  @Test
  void unitDead(){

    controller.initGame(4);
    Tactician player = controller.getTacticians().get(0);
    IUnit unidad = heroFactory.createDefault(player);
    player.setActualUnit(unidad);
    player.setLocationUnit(3, 3);
    Location location = unidad.getLocation();
    assertNotNull(location.getUnit());
    assertTrue(unidad.getLive());
    unidad.die();
    assertFalse(unidad.getLive());
    assertNull(location.getUnit());
  }

  /**
   * Verifica que si un hero de un tactician muere, este debe ser retirado de la mesa
   */
  @Test
  void heroDie(){

    controller.initGame(4);
    Tactician player = controller.getActualPlayer();
    assertTrue(controller.getTacticians().contains(player));
    IUnit hero = null;
    for(int i = 0; i < player.getPlayerUnits().size(); i++){
      if(player.getPlayerUnits().get(i).isHero()){
        hero = player.getPlayerUnits().get(i);
      }
    }
    if(hero!=null){
      hero.die();
    }
    assertFalse(controller.getTacticians().contains(player));
    controller.endTurn();
    assertFalse(controller.getTacticians().contains(player));


  }

  @Test
  void getActualUnit(){

    controller.initGame(4);
    IUnit unit = controller.getActualPlayer().getPlayerUnits().get(0);
    IUnit unit1 = controller.getActualPlayer().getPlayerUnits().get(1);
    IUnit unit2 = heroFactory.createDefault(controller.getTacticians().get(1));
    assertNotEquals(controller.getActualUnit(), unit);
    assertNotEquals(controller.getActualUnit(), unit1);
    controller.getActualPlayer().setActualUnit(unit);
    controller.getActualPlayer().setLocationUnit(0, 0);
    assertEquals(controller.getActualUnit(), unit);
    assertNotEquals(controller.getActualUnit(), unit1);
    controller.getActualPlayer().setActualUnit(unit1);
    assertNotEquals(controller.getActualUnit(), unit);
    assertEquals(controller.getActualUnit(), unit1);
    controller.getActualPlayer().setActualUnit(unit2);
    assertNotEquals(controller.getActualUnit(), unit2);


  }

  /**
   * Verifica que si una unidad se mueve en un turno, no pueda hacerlo más durante el resto del turno
   */
  @Test
  void resetMovement(){

    controller.initGame(4);
    Tactician player = controller.getActualPlayer();
    player.setActualUnit(player.getPlayerUnits().get(0));
    controller.getGameMap().getCell(0, 0).setUnit(player.getActualUnit());
    player.getActualUnit().setLocation(controller.getGameMap().getCell(0, 0));
    assertFalse(player.getActualUnit().getMove());
    player.moveUnit(0, 1);
    assertTrue(player.getActualUnit().getMove());
    controller.endTurn();
    controller.setActualPlayer(player);
    assertFalse(player.getActualUnit().getMove());
  }

  @Test
  void getAlpaca(){

    Tactician player = new Tactician("Player", controller);
    controller.getAlpaca(player);
    IUnit unit = alpacaFactory.createDefault(testTacticians.get(0));
    player.getPlayerUnits().get(0).equalsTo(unit);
  }

  @Test
  void getArcher(){

    Tactician player = new Tactician("Player", controller);
    controller.getArcher(player);
    IUnit unit = archerFactory.createDefault(testTacticians.get(0));
    player.getPlayerUnits().get(0).equalsTo(unit);

  }

  @Test
  void getCleric(){

    Tactician player = new Tactician("Player", controller);
    controller.getCleric(player);
    IUnit unit = clericFactory.createDefault(testTacticians.get(0));
    player.getPlayerUnits().get(0).equalsTo(unit);

  }

  @Test
  void getFighter(){

    Tactician player = new Tactician("Player", controller);
    controller.getFighter(player);
    IUnit unit = fighterFactory.createDefault(testTacticians.get(0));
    player.getPlayerUnits().get(0).equalsTo(unit);

  }

  @Test
  void getHero(){

    Tactician player = new Tactician("Player", controller);
    controller.getHero(player);
    IUnit unit = heroFactory.createDefault(testTacticians.get(0));
    player.getPlayerUnits().get(0).equalsTo(unit);

  }

  @Test
  void getSorcerer(){

    Tactician player = new Tactician("Player", controller);
    controller.getSorcerer(player);
    IUnit unit = sorcererFactory.createDefault(testTacticians.get(0));
    player.getPlayerUnits().get(0).equalsTo(unit);

  }

  @Test
  void getSwordMaster(){

    Tactician player = new Tactician("Player", controller);
    controller.getSwordMaster(player);
    IUnit unit = swordMasterFactory.createDefault(testTacticians.get(0));
    player.getPlayerUnits().get(0).equalsTo(unit);

  }

  /**
   * Verifica que solo una unidad este en una posicion
   */
  @Test
  void OnlyOneUnit(){

    controller.initGame(4);
    controller.getActualPlayer().setActualUnit(controller.getActualPlayer().getPlayerUnits().get(0));
    controller.getActualPlayer().setLocationUnit(0, 0);
    controller.getActualPlayer().setActualUnit(controller.getActualPlayer().getPlayerUnits().get(1));
    controller.getActualPlayer().setLocationUnit(0, 1);
    assertEquals(controller.getActualPlayer().getPlayerUnits().get(0), controller.getGameMap().getCell(0, 0).getUnit());
    assertEquals(controller.getActualPlayer().getPlayerUnits().get(1), controller.getGameMap().getCell(0, 1).getUnit());
    controller.getActualPlayer().getActualUnit().moveTo(controller.getGameMap().getCell(0, 0));
    assertEquals(controller.getActualPlayer().getPlayerUnits().get(0), controller.getGameMap().getCell(0, 0).getUnit());
    assertEquals(controller.getActualPlayer().getPlayerUnits().get(1), controller.getGameMap().getCell(0, 1).getUnit());

  }

  /**
   * Verifica que no se puedan seleccionar unidades enemigas y moverlas
   */

  @Test
  void OnlyMyUnit(){

    controller.initGame(4);
    controller.setActualPlayer(controller.getTacticians().get(0));
    controller.setActualUnit(controller.getTacticians().get(1).getPlayerUnits().get(0));
    controller.getActualPlayer().moveUnit(0, 0);
    assertEquals(controller.getGameMap().getCell(0, 0).getUnit(), null);
    assertEquals(controller.getActualPlayer().getActualUnit(), null);



  }

}