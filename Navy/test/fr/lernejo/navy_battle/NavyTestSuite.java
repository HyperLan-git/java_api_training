package fr.lernejo.navy_battle;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import fr.hyper.battleship.BattleshipGameTest;
import fr.hyper.battleship.BattleshipTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	BattleshipTest.class,
	BattleshipGameTest.class
})

public class NavyTestSuite {

}
