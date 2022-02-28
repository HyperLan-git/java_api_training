package fr.lernejo.navy_battle;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class NavyRunner {
	public static final void main(String[] args) {
		Result result = JUnitCore.runClasses(NavyTestSuite.class);
		for(Failure f : result.getFailures()) {
			System.out.println("You fucked up : " + f.toString());
		}

		System.out.println(result.wasSuccessful() ?
				"Test went well, as expected from the best program ever.":
					"Bad program ! Unacceptable ! Testing failed :(");
	}
}
