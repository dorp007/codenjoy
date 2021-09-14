package com.codenjoy.dojo.loderunner.model.items.enemy;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.games.loderunner.Board;
import com.codenjoy.dojo.loderunner.game.AbstractGameTest;
import com.codenjoy.dojo.loderunner.model.Hero;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.printer.Printer;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import com.codenjoy.dojo.utils.TestUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.codenjoy.dojo.utils.TestUtils.split;
import static org.junit.Assert.assertEquals;

public class AITest extends AbstractGameTest {

    private final AI ai = new AI();

    @Override
    public void setup() {
        super.setup();
        AI.POSSIBLE_IS_CONSTANT = true;
    }

    @Override
    protected void reloadAllEnemies() {
        enemies = field.enemies().stream()
                .map(enemy -> new EnemyJoystick(enemy, ai))
                .collect(Collectors.toList());
    }

    private void assertP(String map, String expected) {
        givenFl(map);

        Map<Point, List<Direction>> result = new TreeMap<>();
        for (Map.Entry<Point, List<Direction>> entry : ai.ways(field).entrySet()) {
            List<Direction> value = entry.getValue();
            if (!value.isEmpty()) {
                result.put(entry.getKey(), value);
            }
        }

        assertEquals(expected, split(result, "], \n["));
    }

    private void assertD(String expected) {
        assertEquals(expected,
                ai.getPath(field, field.enemies().all().get(0), (List) field.heroes().all()).toString());
    }

    @Test
    public void shouldGeneratePossibleWays1() {
        assertP("☼☼☼☼☼" +
                "☼   ☼" +
                "☼ ◄ ☼" +
                "☼###☼" +
                "☼☼☼☼☼",

                "{[1,2]=[RIGHT], \n" +
                "[1,3]=[DOWN], \n" +
                "[2,2]=[LEFT, RIGHT], \n" +
                "[2,3]=[DOWN], \n" +
                "[3,2]=[LEFT], \n" +
                "[3,3]=[DOWN]}");
    }

    @Test
    public void shouldGeneratePossibleWays_withLadder() {
        assertP("☼☼☼☼☼" +
                "☼  H☼" +
                "☼ ◄H☼" +
                "☼###☼" +
                "☼☼☼☼☼",

                "{[1,2]=[RIGHT], \n" +
                "[1,3]=[DOWN], \n" +
                "[2,2]=[LEFT, RIGHT], \n" +
                "[2,3]=[DOWN], \n" +
                "[3,2]=[LEFT, UP], \n" +
                "[3,3]=[LEFT, DOWN]}");
    }

    @Test
    public void shouldGeneratePossibleWays_withPipe() {
        assertP("☼☼☼☼☼" +
                "☼   ☼" +
                "☼◄~~☼" +
                "☼#  ☼" +
                "☼☼☼☼☼",

                "{[1,2]=[RIGHT], \n" +
                "[1,3]=[DOWN], \n" +
                "[2,1]=[RIGHT], \n" +
                "[2,2]=[LEFT, RIGHT, DOWN], \n" +
                "[2,3]=[DOWN], \n" +
                "[3,1]=[LEFT], \n" +
                "[3,2]=[LEFT, DOWN], \n" +
                "[3,3]=[DOWN]}");
    }

    @Test
    public void shouldGeneratePossibleWays_withLadder2() {
        assertP("☼☼☼☼☼" +
                "☼   ☼" +
                "☼◄HH☼" +
                "☼#  ☼" +
                "☼☼☼☼☼",

                "{[1,2]=[RIGHT], \n" +
                "[1,3]=[DOWN], \n" +
                "[2,1]=[RIGHT], \n" +
                "[2,2]=[LEFT, RIGHT, UP, DOWN], \n" +
                "[2,3]=[LEFT, RIGHT, DOWN], \n" +
                "[3,1]=[LEFT], \n" +
                "[3,2]=[LEFT, UP, DOWN], \n" +
                "[3,3]=[LEFT, DOWN]}");
    }

    @Test
    public void shouldGeneratePossibleWays_withoutBorders() {
        assertP("   H" +
                "~~ H" +
                "H  H" +
                "H  H",

                "{[0,0]=[RIGHT, UP], \n" +
                "[0,1]=[RIGHT, UP, DOWN], \n" +
                "[0,2]=[RIGHT, DOWN], \n" +
                "[0,3]=[DOWN], \n" +
                "[1,0]=[LEFT, RIGHT], \n" +
                "[1,1]=[DOWN], \n" +
                "[1,2]=[LEFT, RIGHT, DOWN], \n" +
                "[1,3]=[DOWN], \n" +
                "[2,0]=[LEFT, RIGHT], \n" +
                "[2,1]=[DOWN], \n" +
                "[2,2]=[DOWN], \n" +
                "[2,3]=[DOWN], \n" +
                "[3,0]=[LEFT, UP], \n" +
                "[3,1]=[LEFT, UP, DOWN], \n" +
                "[3,2]=[LEFT, UP, DOWN], \n" +
                "[3,3]=[LEFT, DOWN]}");
    }

    @Test
    public void shouldGeneratePossibleWays_whenFromLadderOnPipe() {
        assertP("☼☼☼☼☼" +
                "☼   ☼" +
                "☼~~H☼" +
                "☼  H☼" +
                "☼☼☼☼☼",

                "{[1,1]=[RIGHT], \n" +
                "[1,2]=[RIGHT, DOWN], \n" +
                "[1,3]=[DOWN], \n" +
                "[2,1]=[LEFT, RIGHT], \n" +
                "[2,2]=[LEFT, RIGHT, DOWN], \n" +
                "[2,3]=[DOWN], \n" +
                "[3,1]=[LEFT, UP], \n" +
                "[3,2]=[LEFT, UP, DOWN], \n" +
                "[3,3]=[LEFT, DOWN]}");
    }

    @Test
    public void shouldGeneratePossibleWays_whenFromLadderOnPipe2() {
        assertP("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼◄~~~ ☼" +
                "☼#  #H☼" +
                "☼    H☼" +
                "☼#####☼" +
                "☼☼☼☼☼☼☼",

                "{[1,2]=[RIGHT], \n" +
                "[1,4]=[RIGHT], \n" +
                "[1,5]=[DOWN], \n" +
                "[2,2]=[LEFT, RIGHT], \n" +
                "[2,3]=[DOWN], \n" +
                "[2,4]=[LEFT, RIGHT, DOWN], \n" +
                "[2,5]=[DOWN], \n" +
                "[3,2]=[LEFT, RIGHT], \n" +
                "[3,3]=[DOWN], \n" +
                "[3,4]=[LEFT, RIGHT, DOWN], \n" +
                "[3,5]=[DOWN], \n" +
                "[4,2]=[LEFT, RIGHT], \n" +
                "[4,4]=[LEFT, RIGHT], \n" +
                "[4,5]=[DOWN], \n" +
                "[5,2]=[LEFT, UP], \n" +
                "[5,3]=[UP, DOWN], \n" +
                "[5,4]=[LEFT, DOWN], \n" +
                "[5,5]=[DOWN]}");

    }

    @Test
    public void shouldGeneratePossibleWays_whenFromLadderOnPipe3() {
        assertP("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼◄~~~H☼" +
                "☼#   H☼" +
                "☼    H☼" +
                "☼#####☼" +
                "☼☼☼☼☼☼☼",

                "{[1,2]=[RIGHT], \n" +
                "[1,4]=[RIGHT], \n" +
                "[1,5]=[DOWN], \n" +
                "[2,2]=[LEFT, RIGHT], \n" +
                "[2,3]=[DOWN], \n" +
                "[2,4]=[LEFT, RIGHT, DOWN], \n" +
                "[2,5]=[DOWN], \n" +
                "[3,2]=[LEFT, RIGHT], \n" +
                "[3,3]=[DOWN], \n" +
                "[3,4]=[LEFT, RIGHT, DOWN], \n" +
                "[3,5]=[DOWN], \n" +
                "[4,2]=[LEFT, RIGHT], \n" +
                "[4,3]=[DOWN], \n" +
                "[4,4]=[LEFT, RIGHT, DOWN], \n" +
                "[4,5]=[DOWN], \n" +
                "[5,2]=[LEFT, UP], \n" +
                "[5,3]=[LEFT, UP, DOWN], \n" +
                "[5,4]=[LEFT, UP, DOWN], \n" +
                "[5,5]=[LEFT, DOWN]}");
    }

    @Test
    public void shouldOnlyLeft() {
        givenFl("☼☼☼☼☼" +
                "☼   ☼" +
                "☼◄ «☼" +
                "☼###☼" +
                "☼☼☼☼☼");

        assertD("[LEFT, LEFT]");

        assertQ(" ☼ ☼ ☼ ☼ ☼\n" +
                "          \n" +
                " ☼ . . . ☼\n" +
                "          \n" +
                " ☼ ◄←.←« ☼\n" +
                "          \n" +
                " ☼ # # # ☼\n" +
                "          \n" +
                " ☼ ☼ ☼ ☼ ☼\n" +
                "          \n");
    }

    @Test
    public void shouldOnlyRight() {
        givenFl("☼☼☼☼☼" +
                "☼   ☼" +
                "☼« ◄☼" +
                "☼###☼" +
                "☼☼☼☼☼");

        assertD("[RIGHT, RIGHT]");

        assertQ(" ☼ ☼ ☼ ☼ ☼\n" +
                "          \n" +
                " ☼ . . . ☼\n" +
                "          \n" +
                " ☼ «→.→◄ ☼\n" +
                "          \n" +
                " ☼ # # # ☼\n" +
                "          \n" +
                " ☼ ☼ ☼ ☼ ☼\n" +
                "          \n");
    }

    @Test
    public void shouldUpWithLadder() {
        givenFl("☼☼☼☼☼☼" +
                "☼   ◄☼" +
                "☼   H☼" +
                "☼«  H☼" +
                "☼####☼" +
                "☼☼☼☼☼☼");

        assertD("[RIGHT, RIGHT, RIGHT, UP, UP]");

        assertQ(" ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "            \n" +
                " ☼ . . . ◄ ☼\n" +
                "         ↑  \n" +
                " ☼ . . . H ☼\n" +
                "         ↑  \n" +
                " ☼ «→.→.→H ☼\n" +
                "            \n" +
                " ☼ # # # # ☼\n" +
                "            \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "            \n");
    }

    @Test
    public void shouldUpWithLadderThenPipe() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼◄~~~ ☼" +
                "☼#  #H☼" +
                "☼«   H☼" +
                "☼#####☼" +
                "☼☼☼☼☼☼☼");

        assertD("[RIGHT, RIGHT, RIGHT, RIGHT, UP, UP, LEFT, LEFT, LEFT, LEFT]");

        assertQ(" ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n" +
                " ☼ . . . . . ☼\n" +
                "              \n" +
                " ☼ ◄←~←~←~←. ☼\n" +
                "           ↑  \n" +
                " ☼ # . . # H ☼\n" +
                "           ↑  \n" +
                " ☼ «→.→.→.→H ☼\n" +
                "              \n" +
                " ☼ # # # # # ☼\n" +
                "              \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n");
    }

    @Test
    public void shouldUpWithLadderThenPipe2() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼◄~~~H☼" +
                "☼#   H☼" +
                "☼«   H☼" +
                "☼#####☼" +
                "☼☼☼☼☼☼☼");

        assertD("[RIGHT, RIGHT, RIGHT, RIGHT, UP, UP, LEFT, LEFT, LEFT, LEFT]");

        assertQ(" ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n" +
                " ☼ . . . . . ☼\n" +
                "              \n" +
                " ☼ ◄←~←~←~←H ☼\n" +
                "           ↑  \n" +
                " ☼ # . . . H ☼\n" +
                "           ↑  \n" +
                " ☼ «→.→.→.→H ☼\n" +
                "              \n" +
                " ☼ # # # # # ☼\n" +
                "              \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n");
    }

    @Test
    public void shouldTwoEqualsWays() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ~◄~ ☼" +
                "☼H # H☼" +
                "☼H « H☼" +
                "☼#####☼" +
                "☼☼☼☼☼☼☼");

        assertD("[LEFT, LEFT, UP, UP, RIGHT, RIGHT]");

        assertQ(" ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n" +
                " ☼ . . . . . ☼\n" +
                "              \n" +
                " ☼ .→~→◄ ~ . ☼\n" +
                "   ↑          \n" +
                " ☼ H . # . H ☼\n" +
                "   ↑          \n" +
                " ☼ H←.←« . H ☼\n" +
                "              \n" +
                " ☼ # # # # # ☼\n" +
                "              \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n");
    }

    @Test
    public void shouldTwoNotEqualsWays() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ◄~~ ☼" +
                "☼H#  H☼" +
                "☼H « H☼" +
                "☼#####☼" +
                "☼☼☼☼☼☼☼");

        assertD("[LEFT, LEFT, UP, UP, RIGHT]");

        assertQ(" ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n" +
                " ☼ . . . . . ☼\n" +
                "              \n" +
                " ☼ .→◄ ~ ~ . ☼\n" +
                "   ↑          \n" +
                " ☼ H # . . H ☼\n" +
                "   ↑          \n" +
                " ☼ H←.←« . H ☼\n" +
                "              \n" +
                " ☼ # # # # # ☼\n" +
                "              \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n");
    }

    @Test
    public void shouldTwoNotEqualsWays2() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼ ◄~~ ☼" +
                "☼H#  H☼" +
                "☼H  «H☼" +
                "☼#####☼" +
                "☼☼☼☼☼☼☼");

        assertD("[LEFT, LEFT, LEFT, UP, UP, RIGHT]");

        assertQ(" ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n" +
                " ☼ . . . . . ☼\n" +
                "              \n" +
                " ☼ .→◄ ~ ~ . ☼\n" +
                "   ↑          \n" +
                " ☼ H # . . H ☼\n" +
                "   ↑          \n" +
                " ☼ H←.←.←« H ☼\n" +
                "              \n" +
                " ☼ # # # # # ☼\n" +
                "              \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n");
    }

    @Test
    public void shouldTwoNotEqualsWays3() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼     ☼" +
                "☼  ◄~ ☼" +
                "☼H## H☼" +
                "☼H  «H☼" +
                "☼#####☼" +
                "☼☼☼☼☼☼☼");

        assertD("[RIGHT, UP, UP, LEFT, LEFT]");

        assertQ(" ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n" +
                " ☼ . . . . . ☼\n" +
                "              \n" +
                " ☼ . . ◄←~←. ☼\n" +
                "           ↑  \n" +
                " ☼ H # # . H ☼\n" +
                "           ↑  \n" +
                " ☼ H . . «→H ☼\n" +
                "              \n" +
                " ☼ # # # # # ☼\n" +
                "              \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n");
    }

    @Test
    public void shouldOnPit() {
        givenFl("☼☼☼☼☼☼☼" +
                "☼#####☼" +
                "☼ « « ☼" +
                "☼◄###◄☼" +
                "☼#####☼" +
                "☼#####☼" +
                "☼☼☼☼☼☼☼");

        Enemy enemy1 = field.enemies().all().get(0);
        assertEquals("[2,4]", enemy1.toString());

        Hero hero1 = field.heroes().all().get(0);
        assertEquals("[1,3]", hero1.toString());

        Enemy enemy2 = field.enemies().all().get(1);
        assertEquals("[4,4]", enemy2.toString());

        Hero hero2 = field.heroes().all().get(1);
        assertEquals("[5,3]", hero2.toString());

        assertEquals(Direction.RIGHT, ai.getDirection(field, enemy1, Arrays.asList(hero2)));
        assertEquals("[RIGHT, RIGHT, RIGHT, DOWN]", ai.getPath(field, enemy1, Arrays.asList(hero2)).toString());

        assertW(enemy1, Arrays.asList(hero2),
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n" +
                " ☼ # # # # # ☼\n" +
                "              \n" +
                " ☼ . «→.→«→. ☼\n" +
                "           ↓  \n" +
                " ☼ ◄ # # # ) ☼\n" +
                "              \n" +
                " ☼ # # # # # ☼\n" +
                "              \n" +
                " ☼ # # # # # ☼\n" +
                "              \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n");

        assertEquals(Direction.LEFT, ai.getDirection(field, enemy2, Arrays.asList(hero1)));
        assertEquals("[LEFT, LEFT, LEFT, DOWN]", ai.getPath(field, enemy2, Arrays.asList(hero1)).toString());

        assertW(enemy2, Arrays.asList(hero1),
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n" +
                " ☼ # # # # # ☼\n" +
                "              \n" +
                " ☼ .←«←.←« . ☼\n" +
                "   ↓          \n" +
                " ☼ ◄ # # # ) ☼\n" +
                "              \n" +
                " ☼ # # # # # ☼\n" +
                "              \n" +
                " ☼ # # # # # ☼\n" +
                "              \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "              \n");
    }

    @Test
    public void shouldTwoEnemiesWithTwoHero() {
        givenFl("☼☼☼☼☼☼☼☼" +
                "☼ ◄  ◄ ☼" +
                "☼H#  #H☼" +
                "☼H    H☼" +
                "☼H    H☼" +
                "☼H «« H☼" +
                "☼######☼" +
                "☼☼☼☼☼☼☼☼");

        // проверяем следующую команду для первого чертика
        Enemy enemy1 = field.enemies().all().get(0);
        Hero hero1 = field.heroes().all().get(0);
        assertEquals("[3,2]", enemy1.toString());
        assertEquals(Direction.LEFT, ai.getDirection(field, enemy1, Arrays.asList(hero1)));

        // проверяем весь путь для первого чертика
        assertEquals("[2,6]", hero1.toString());
        assertEquals("[LEFT, LEFT, UP, UP, UP, UP, RIGHT]", ai.getPath(field, enemy1, Arrays.asList(hero1)).toString());

        assertW(enemy1, Arrays.asList(hero1),
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "                \n" +
                " ☼ .→◄ . . ) . ☼\n" +
                "   ↑            \n" +
                " ☼ H # . . # H ☼\n" +
                "   ↑            \n" +
                " ☼ H . . . . H ☼\n" +
                "   ↑            \n" +
                " ☼ H . . . . H ☼\n" +
                "   ↑            \n" +
                " ☼ H←.←« « . H ☼\n" +
                "                \n" +
                " ☼ # # # # # # ☼\n" +
                "                \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "                \n");

        assertW(enemy1, Arrays.asList(hero1),
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "                \n" +
                " ☼ .→◄ . . ) . ☼\n" +
                "   ↑            \n" +
                " ☼ H # . . # H ☼\n" +
                "   ↑            \n" +
                " ☼ H . . . . H ☼\n" +
                "   ↑            \n" +
                " ☼ H . . . . H ☼\n" +
                "   ↑            \n" +
                " ☼ H←.←« « . H ☼\n" +
                "                \n" +
                " ☼ # # # # # # ☼\n" +
                "                \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "                \n");

        // проверяем следующую команду для второго чертика
        Enemy enemy2 = field.enemies().all().get(1);
        Hero hero2 = field.heroes().all().get(1);
        assertEquals("[4,2]", enemy2.toString());
        assertEquals(Direction.RIGHT, ai.getDirection(field, enemy2, Arrays.asList(hero2)));

        // проверяем весь путь для второго чертика
        assertEquals("[5,6]", hero2.toString());
        assertEquals("[RIGHT, RIGHT, UP, UP, UP, UP, LEFT]", ai.getPath(field, enemy2, Arrays.asList(hero2)).toString());

        assertW(enemy2, Arrays.asList(hero2),
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "                \n" +
                " ☼ . ◄ . . )←. ☼\n" +
                "             ↑  \n" +
                " ☼ H # . . # H ☼\n" +
                "             ↑  \n" +
                " ☼ H . . . . H ☼\n" +
                "             ↑  \n" +
                " ☼ H . . . . H ☼\n" +
                "             ↑  \n" +
                " ☼ H . « «→.→H ☼\n" +
                "                \n" +
                " ☼ # # # # # # ☼\n" +
                "                \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "                \n");
    }

    // из за того, что чертики друг для друга препятствие - не каждый чертик может охотится за любым героем
    // но они будут пытаться, в надежде, что другой чертик не будет стоять на месте
    // TODO тут возможен случай, когда они друг друга анигилируют :)
    @Test
    public void shouldTwoEnemiesWithTwoHero_enemyIsBarrier() {
        givenFl("☼☼☼☼☼☼☼☼" +
                "☼ ◄  ◄ ☼" +
                "☼H#  #H☼" +
                "☼H    H☼" +
                "☼H    H☼" +
                "☼H «« H☼" +
                "☼######☼" +
                "☼☼☼☼☼☼☼☼");

        // пробуем чтобы первый чертик пошел за вторым игроком
        Enemy enemy2 = field.enemies().all().get(1);
        assertEquals("[4,2]", enemy2.toString());
        Hero hero1 = field.heroes().all().get(0);
        assertEquals("[2,6]", hero1.toString());
        assertEquals("[LEFT, LEFT, LEFT, UP, UP, UP, UP, RIGHT]", ai.getPath(field, enemy2, Arrays.asList(hero1)).toString());

        assertW(enemy2, Arrays.asList(hero1),
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "                \n" +
                " ☼ .→◄ . . ) . ☼\n" +
                "   ↑            \n" +
                " ☼ H # . . # H ☼\n" +
                "   ↑            \n" +
                " ☼ H . . . . H ☼\n" +
                "   ↑            \n" +
                " ☼ H . . . . H ☼\n" +
                "   ↑            \n" +
                " ☼ H←.←«←« . H ☼\n" +
                "                \n" +
                " ☼ # # # # # # ☼\n" +
                "                \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "                \n");

        assertW(enemy2, Arrays.asList(hero1),
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "                \n" +
                " ☼ .→◄ . . ) . ☼\n" +
                "   ↑            \n" +
                " ☼ H # . . # H ☼\n" +
                "   ↑            \n" +
                " ☼ H . . . . H ☼\n" +
                "   ↑            \n" +
                " ☼ H . . . . H ☼\n" +
                "   ↑            \n" +
                " ☼ H←.←«←« . H ☼\n" +
                "                \n" +
                " ☼ # # # # # # ☼\n" +
                "                \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "                \n");

        // пробуем чтобы второй чертик пошел за первым игроком
        Enemy enemy1 = field.enemies().all().get(0);
        assertEquals("[3,2]", enemy1.toString());
        Hero hero2 = field.heroes().all().get(1);
        assertEquals("[5,6]", hero2.toString());
        assertEquals("[RIGHT, RIGHT, RIGHT, UP, UP, UP, UP, LEFT]", ai.getPath(field, enemy1, Arrays.asList(hero2)).toString());

        assertW(enemy1, Arrays.asList(hero2),
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "                \n" +
                " ☼ . ◄ . . )←. ☼\n" +
                "             ↑  \n" +
                " ☼ H # . . # H ☼\n" +
                "             ↑  \n" +
                " ☼ H . . . . H ☼\n" +
                "             ↑  \n" +
                " ☼ H . . . . H ☼\n" +
                "             ↑  \n" +
                " ☼ H . «→«→.→H ☼\n" +
                "                \n" +
                " ☼ # # # # # # ☼\n" +
                "                \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "                \n");
    }

    private void assertQ(String expected) {
        Enemy enemy = field.enemies().all().get(0);
        List<Point> heroes = (List) field.heroes().all();
        assertW(enemy, heroes, expected);
    }

    private void assertW(Point from, List<Point> to, String expected) {
        List<Direction> path = ai.getPath(field, from, to);
        Printer printer = new PrinterFactoryImpl<>().getPrinter(field.reader(),
                field.players().iterator().next());
        Board board = (Board) new Board().forString(printer.print().toString());

        String actual = TestUtils.drawShortestWay(
                from,
                path,
                field.size(),
                pt -> board.getAt(pt).getChar());

        assertEquals(expected, actual);
    }

    @Test
    public void performanceTest() {
        AI.POSSIBLE_IS_CONSTANT = true;

        // about 7s
        givenFl("☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼" +
                "☼     ◄                ◄      ~~~~~~~~~      ◄    ~~~~~~~☼" +
                "☼##H########################H#H       H##########H       ☼" +
                "☼  H            ◄           H######H  H          H#☼☼☼☼☼#☼" +
                "☼H☼☼#☼☼H    H#########H     H#     H#####H#####H##  ~~~~~☼" +
                "☼H     H    H         H#####H#     H ~   H     H  ~~     ☼" +
                "☼H#☼#☼#H    H         H  ~~~ #####H#     H     H    ~~   ☼" +
                "☼H  ~  H~~~~H~~~~~~   H           H   H######H##      ~~ ☼" +
                "☼H     H    H     H###☼☼☼☼☼☼H☼    H~~~H      H          #☼" +
                "☼H     H    H#####H         H     H      H#########H     ☼" +
                "☼☼###☼##☼##☼H         H###H## ◄  H##     H#       ##     ☼" +
                "☼☼###☼~~~~  H         H   H######H######### H###H #####H#☼" +
                "☼☼   ☼      H   ~~~~~~H   H      H          H# #H      H ☼" +
                "☼########H###☼☼☼☼     H  ############   ###### ##########☼" +
                "☼        H     ◄      H                           ◄      ☼" +
                "☼H##########################H########~~~####H############☼" +
                "☼H                 ~~~      H               H            ☼" +
                "☼#######H#######            H###~~~~      ############H  ☼" +
                "☼       H~~~~~~~~~~         H                         H  ☼" +
                "☼       H    ##H   #######H##########~~~~~~~H######## H  ☼" +
                "☼   ◄   H    ##H          H                 H         H  ☼" +
                "☼##H#####    ########H#######~~~~  ~~~#########~~~~~  H  ☼" +
                "☼  H                 H                            ~~~~H  ☼" +
                "☼#########H##########H        #☼☼☼☼☼☼#   ☼☼☼☼☼☼☼      H  ☼" +
                "☼         H          H        ~      ~      ◄         H  ☼" +
                "☼☼☼       H~~~~~~~~~~H      «  ######   ###########   H  ☼" +
                "☼    H######         #######H           ~~~~~~~~~~~~~~H  ☼" +
                "☼H☼  H                      H  H####H                 H  ☼" +
                "☼H☼☼#☼☼☼☼☼☼☼☼☼☼☼☼###☼☼☼☼☼☼☼☼H☼☼☼☼☼☼☼☼#☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼#☼" +
                "☼H            ~~H~~~~☼☼☼☼☼☼☼H☼☼☼☼☼☼☼       H   ~~~~~~~~~H☼" +
                "☼H~~~~  ######  H         H☼H☼H        ####H  ☼         H☼" +
                "☼H    ◄         ##H#######H☼H☼H######H     ###☼☼☼☼☼☼☼☼ ~H☼" +
                "☼H#########       H    ~~~H☼H☼H~~~   H~~~~~ ##        ~ H☼" +
                "☼H        ###H####H##H ◄   ☼H☼       H     ###☼☼☼☼☼☼ ~  H☼" +
                "☼H           H      #######☼H☼#####  H#####   ~~~~~~~ ~ H☼" +
                "☼~~~~~~~~~~~~H       H~~~~~☼H☼~~~~~  H  ◄          ~ ~  H☼" +
                "☼     H              H     ☼H☼     ##########H    ◄     H☼" +
                "☼ ### #############H H#####☼H☼               H ######## H☼" +
                "☼H                 H       ☼H☼#######        H          H☼" +
                "☼H#####   ◄     H##H####                ###H#########   H☼" +
                "☼H      H######### H   ############        H            H☼" +
                "☼H##    H          H~~~~~~                 H #######H## H☼" +
                "☼~~~~#####H#   ~~~~H         ########H     H        H   H☼" +
                "☼     ◄   H        H      ~~~~~~~~   H  ◄  H        H   H☼" +
                "☼   ########H    ######H##        ##############    H   H☼" +
                "☼           H          H        ~~~~~           ##H#####H☼" +
                "☼H    ###########H     H#####H         H##H       H     H☼" +
                "☼H###  ◄         H     H     ###########  ##H###  H     H☼" +
                "☼H  ######  ##H######  H                    H   ##H###  H☼" +
                "☼H            H ~~~~~##H###H     #########H##           H☼" +
                "☼    H########H#       H   ######         H             H☼" +
                "☼ ###H  ◄     H         ~~~~~H      ##H###H####H###     H☼" +
                "☼    H########H#########     H        H        H        H☼" +
                "☼H   H                       H    ◄   H        H  ◄     H☼" +
                "☼H  ####H######         #####H########H##      H#####   H☼" +
                "☼H      H      H#######H                       H        H☼" +
                "☼##############H       H#################################☼" +
                "☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼☼");

        assertQ(" ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "                                                                                                                    \n" +
                " ☼ . . . . . ◄ . . . . . . . . . . . . . . . . ) . . . . . . ~ ~ ~ ~ ~ ~ ~ ~ ~ . . . . . . ) . . . . ~ ~ ~ ~ ~ ~ ~ ☼\n" +
                "                                                                                                                    \n" +
                " ☼ # # H # # # # # # # # # # # # # # # # # # # # # # # # H # H . . . . . . . H # # # # # # # # # # H . . . . . . . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ . . H . . . . . . . . . . . . ) . . . . . . . . . . . H # # # # # # H . . H . . . . . . . . . . H # ☼ ☼ ☼ ☼ ☼ # ☼\n" +
                "                                                                                                                    \n" +
                " ☼ H ☼ ☼ # ☼ ☼ H . . . . H # # # # # # # # # H . . . . . H # . . . . . H # # # # # H # # # # # H # # . . ~ ~ ~ ~ ~ ☼\n" +
                "                                                                                                                    \n" +
                " ☼ H . . . . . H . . . . H . . . . . . . . . H # # # # # H # . . . . . H . ~ . . . H . . . . . H . . ~ ~ . . . . . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ H # ☼ # ☼ # H . . . . H . . . . . . . . . H . . ~ ~ ~ . # # # # # H # . . . . . H . . . . . H . . . . ~ ~ . . . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ H . . ~ . . H ~ ~ ~ ~ H ~ ~ ~ ~ ~ ~ . . . H . . . . . . . . . . . H . . . H # # # # # # H # # . . . . . . ~ ~ . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ H . . . . . H . . . . H . . . . . H # # # ☼ ☼ ☼ ☼ ☼ ☼ H ☼ . . . . H ~ ~ ~ H . . . . . . H . . . . . . . . . . # ☼\n" +
                "                                                                                                                    \n" +
                " ☼ H . . . . . H . . . . H # # # # # H . . . . . . . . . H . . . . . H . . . . . . H # # # # # # # # # H . . . . . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ ☼ # # # ☼ # # ☼ # # ☼ H . . . . . . . . . H # # # H # # . ) . . H # # . . . . . H # . . . . . . . # # . . . . . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ ☼ # # # ☼ ~ ~ ~ ~ . . H . . . . . . . . . H . . . H # # # # # # H # # # # # # # # # . H # # # H . # # # # # H # ☼\n" +
                "                                                                                                                    \n" +
                " ☼ ☼ . . . ☼ . . . . . . H . . . ~ ~ ~ ~ ~ ~ H . . . H . . . . . . H . . . . . . . . . . H # . # H . . . . . . H . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ # # # # # # # # H # # # ☼ ☼ ☼ ☼ . . . . . H . . # # # # # # # # # # # # . . . # # # # # # . # # # # # # # # # # ☼\n" +
                "                                                                                                                    \n" +
                " ☼ . . . . . . . . H . . . . . ) . . . . . . H . . . . . . . . . . . . . . . . . . . . . . . . . . . ) . . . . . . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ H # # # # # # # # # # # # # # # # # # # # # # # # # # H # # # # # # # # ~ ~ ~ # # # # H # # # # # # # # # # # # ☼\n" +
                "                                                                                                                    \n" +
                " ☼ H . . . . . . . . . . . . . . . . . ~ ~ ~ . . . . . . H . . . . . . . . . . . . . . . H . . . . . . . . . . . . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ # # # # # # # H # # # # # # # . . . . . . . . . . . . H # # # ~ ~ ~ ~ . . . . . . # # # # # # # # # # # # H . . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ . . . . . . . H ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ . . . . . . . . . H . . . . . . . . . . . . . . . . . . . . . . . . . H . . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ . . . . . . . H . . . . # # H . . . # # # # # # # H # # # # # # # # # # ~ ~ ~ ~ ~ ~ ~ H # # # # # # # # . H . . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ . . . ) . . . H . . . . # # H . . . . . . . . . . H . . . . . . . . . . . . . . . . . H . . . . . . . . . H . . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ # # H # # # # # . . . . # # # # # # # # H # # # # # # # ~ ~ ~ ~ . . ~ ~ ~ # # # # # # # # # ~ ~ ~ ~ ~ . . H . . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ . . H . . . . . . . . . . . . . . . . . H . . . . . . . . . . . . . . . . . . . . . . . . . . . . ~ ~ ~ ~ H . . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ # # # # # # # # # H # # # # # # # # # # H . . . . . . . . # ☼ ☼ ☼ ☼ ☼ ☼ # . . . ☼ ☼ ☼ ☼ ☼ ☼ ☼ . . . . . . H . . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ . . . . . . . . . H . . . . . . . . . . H . . . . . . . . ~ . . . . . . ~ . . . . . . ) . . . . . . . . . H . . ☼\n" +
                "                                                                                                                    \n" +
                " ☼ ☼ ☼ . . . . . . . H ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ H . . . . . . « . . # # # # # # . . . # # # # # # # # # # # . . . H . . ☼\n" +
                "                                                         ↓                                                          \n" +
                " ☼ . . . . H # # # # # # . . . . . . . . . # # # # # # # H . . . . . . . . . . . ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ H . . ☼\n" +
                "                                                         ↓                                                          \n" +
                " ☼ H ☼ . . H . . . . . . . . . . . . . . . . . . . . . . H . . H # # # # H . . . . . . . . . . . . . . . . . H . . ☼\n" +
                "                                                         ↓                                                          \n" +
                " ☼ H ☼ ☼ # ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ # # # ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ H ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ # ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ # ☼\n" +
                "                                                         ↓                                                          \n" +
                " ☼ H . . . . . . . . . . . . ~ ~ H ~ ~ ~ ~ ☼ ☼ ☼ ☼ ☼ ☼ ☼ H ☼ ☼ ☼ ☼ ☼ ☼ ☼ . . . . . . . H . . . ~ ~ ~ ~ ~ ~ ~ ~ ~ H ☼\n" +
                "                                                         ↓                                                          \n" +
                " ☼ H ~ ~ ~ ~ . . # # # # # # . . H . . . . . . . . . H ☼ H ☼ H . . . . . . . . # # # # H . . ☼ . . . . . . . . . H ☼\n" +
                "                                                         ↓                                                          \n" +
                " ☼ H . . . . ) . . . . . . . . . # # H # # # # # # # H ☼ H ☼ H # # # # # # H . . . . . # # # ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ . ~ H ☼\n" +
                "                                                         ↓                                                          \n" +
                " ☼ H # # # # # # # # # . . . . . . . H . . . . ~ ~ ~ H ☼ H ☼ H ~ ~ ~ . . . H ~ ~ ~ ~ ~ . # # . . . . . . . . ~ . H ☼\n" +
                "                                                         ↓                                                          \n" +
                " ☼ H . . . . . . . . # # # H # # # # H # # H . ) . . . ☼ H ☼ . . . . . . . H . . . . . # # # ☼ ☼ ☼ ☼ ☼ ☼ . ~ . . H ☼\n" +
                "                                                         ↓                                                          \n" +
                " ☼ H . . . . . . . . . . . H . . . . . . # # # # # # # ☼ H ☼ # # # # # . . H # # # # # . . . ~ ~ ~ ~ ~ ~ ~ . ~ . H ☼\n" +
                "                                                         ↓                                                          \n" +
                " ☼ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ H . . . . . . . H ~ ~ ~ ~ ~ ☼ H ☼ ~ ~ ~ ~ ~ . . H . . ) . . . . . . . . . . ~ . ~ . . H ☼\n" +
                "                                                         ↓                                                          \n" +
                " ☼ . . . . . H . . . . . . . . . . . . . . H . . . . . ☼ H ☼ . . . . . # # # # # # # # # # H . . . . ) . . . . . H ☼\n" +
                "                                                         ↓                                                          \n" +
                " ☼ . # # # . # # # # # # # # # # # # # H . H # # # # # ☼ H ☼ . . . . . . . . . . . . . . . H . # # # # # # # # . H ☼\n" +
                "                                                         ↓                                                          \n" +
                " ☼ H . . . . . . . . . . . . . . . . . H . . . . . . . ☼ H ☼ # # # # # # # . . . . . . . . H . . . . . . . . . . H ☼\n" +
                "                                                         ↓                                                          \n" +
                " ☼ H # # # # # . . . ) . . . . . H # # H # # # # . . . . .→.→.→.→.→.→.→. . . . . # # # H # # # # # # # # # . . . H ☼\n" +
                "                                                                       ↓                                            \n" +
                " ☼ H . . . . . . H # # # # # # # # # . H . . . # # # # # # # # # # # # . . . . . . . . H . . . . . . . . . . . . H ☼\n" +
                "                                                                       ↓                                            \n" +
                " ☼ H # # . . . . H . . . . . . . . . . H ~ ~ ~ ~ ~ ~ . . . . . . . . . .→.→.→. . . . . H . # # # # # # # H # # . H ☼\n" +
                "                                                                             ↓                                      \n" +
                " ☼ ~ ~ ~ ~ # # # # # H # . . . ~ ~ ~ ~ H . . . . . . . . . # # # # # # # # H . . . . . H . . . . . . . . H . . . H ☼\n" +
                "                                                                             ↓                                      \n" +
                " ☼ . . . . . ) . . . H . . . . . . . . H . . . . . . ~ ~ ~ ~ ~ ~ ~ ~ . . . H .→.→) . . H . . . . . . . . H . . . H ☼\n" +
                "                                                                                                                    \n" +
                " ☼ . . . # # # # # # # # H . . . . # # # # # # H # # . . . . . . . . # # # # # # # # # # # # # # . . . . H . . . H ☼\n" +
                "                                                                                                                    \n" +
                " ☼ . . . . . . . . . . . H . . . . . . . . . . H . . . . . . . . ~ ~ ~ ~ ~ . . . . . . . . . . . # # H # # # # # H ☼\n" +
                "                                                                                                                    \n" +
                " ☼ H . . . . # # # # # # # # # # # H . . . . . H # # # # # H . . . . . . . . . H # # H . . . . . . . H . . . . . H ☼\n" +
                "                                                                                                                    \n" +
                " ☼ H # # # . . ) . . . . . . . . . H . . . . . H . . . . . # # # # # # # # # # # . . # # H # # # . . H . . . . . H ☼\n" +
                "                                                                                                                    \n" +
                " ☼ H . . # # # # # # . . # # H # # # # # # . . H . . . . . . . . . . . . . . . . . . . . H . . . # # H # # # . . H ☼\n" +
                "                                                                                                                    \n" +
                " ☼ H . . . . . . . . . . . . H . ~ ~ ~ ~ ~ # # H # # # H . . . . . # # # # # # # # # H # # . . . . . . . . . . . H ☼\n" +
                "                                                                                                                    \n" +
                " ☼ . . . . H # # # # # # # # H # . . . . . . . H . . . # # # # # # . . . . . . . . . H . . . . . . . . . . . . . H ☼\n" +
                "                                                                                                                    \n" +
                " ☼ . # # # H . . ) . . . . . H . . . . . . . . . ~ ~ ~ ~ ~ H . . . . . . # # H # # # H # # # # H # # # . . . . . H ☼\n" +
                "                                                                                                                    \n" +
                " ☼ . . . . H # # # # # # # # H # # # # # # # # # . . . . . H . . . . . . . . H . . . . . . . . H . . . . . . . . H ☼\n" +
                "                                                                                                                    \n" +
                " ☼ H . . . H . . . . . . . . . . . . . . . . . . . . . . . H . . . . ) . . . H . . . . . . . . H . . ) . . . . . H ☼\n" +
                "                                                                                                                    \n" +
                " ☼ H . . # # # # H # # # # # # . . . . . . . . . # # # # # H # # # # # # # # H # # . . . . . . H # # # # # . . . H ☼\n" +
                "                                                                                                                    \n" +
                " ☼ H . . . . . . H . . . . . . H # # # # # # # H . . . . . . . . . . . . . . . . . . . . . . . H . . . . . . . . H ☼\n" +
                "                                                                                                                    \n" +
                " ☼ # # # # # # # # # # # # # # H . . . . . . . H # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # ☼\n" +
                "                                                                                                                    \n" +
                " ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼\n" +
                "                                                                                                                    \n");

        for (int i = 0; i < 10000; i++) {
            assertD("[DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, DOWN, " +
                    "DOWN, DOWN, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, DOWN, DOWN, " +
                    "RIGHT, RIGHT, RIGHT, DOWN, DOWN, RIGHT, RIGHT]");
        }
    }
}