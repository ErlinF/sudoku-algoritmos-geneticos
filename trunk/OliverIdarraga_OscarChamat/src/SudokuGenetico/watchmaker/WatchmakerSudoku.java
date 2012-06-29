// ============================================================================
//   Copyright 2006, 2007 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package SudokuGenetico.watchmaker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.selection.TournamentSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;
import org.uncommons.watchmaker.framework.termination.TargetFitness;
import org.uncommons.watchmaker.swing.AbortControl;
import rutas.GetRoutes;

/**
 * @author 
 */
public class WatchmakerSudoku {

    private static final String[] BLANK_PUZZLE = new String[]{".........",
        ".........",
        ".........",
        ".........",
        ".........",
        ".........",
        ".........",
        ".........",
        "........."};
    private static final String[] EASY_PUZZLE = new String[]{"4.5...9.7",
        ".2..9..6.",
        "39.6.7.28",
        "9..3.2..6",
        "7..9.6..3",
        "5..4.8..1",
        "28.1.5.49",
        ".7..3..8.",
        "6.4...3.2"};
    private static final String[] MEDIUM_PUZZLE = new String[]{"....3....",
        ".....6293",
        ".2.9.48..",
        ".754...38",
        "..46.71..",
        "91...547.",
        "..38.9.1.",
        "1567.....",
        "....1...."};
    private static final String[] HARD_PUZZLE = new String[]{"...891...",
        "....5.8..",
        ".....6.2.",
        "5....4..8",
        "49....67.",
        "8.13....5",
        ".6..8..9.",
        "..5.4.2.7",
        "...1.3.8."};
    private static final String[][] PUZZLES = new String[][]{EASY_PUZZLE,
        MEDIUM_PUZZLE,
        HARD_PUZZLE,
        BLANK_PUZZLE};
    //private final SelectionStrategy<Object> selectionStrategy = new TournamentSelection(selectionPressure.getNumberGenerator());
    private final int POBLACION = 100, GENERACIONES = 60;
    private Scanner sc;
    private int n;
    private int nn;
    private Coordenadas c;

    public WatchmakerSudoku() throws IOException {

        sc = new Scanner(new File(GetRoutes.escogerRutaArchivo()));
        n = sc.nextInt();
//        dbg(n);
        nn = n * n;
        c = new Coordenadas(n);
//        add(createControls(), BorderLayout.NORTH);
//        add(sudokuView, BorderLayout.CENTER);
//        add(createStatusBar(), BorderLayout.SOUTH);
//        sudokuView.setPuzzle(EASY_PUZZLE);
    }

    protected void solveSudoku() {
        SudokuFactory cf = new SudokuFactory(n, sc, c);

        EvolutionaryOperator a = new SudokuMutacion();

        List<EvolutionaryOperator<Sudoku>> operadores = new ArrayList<EvolutionaryOperator<Sudoku>>(2);

        operadores.add(new SudokuReproduccion());
        operadores.add(new SudokuMutacion());

        EvolutionaryOperator<Sudoku> pipeline = new EvolutionPipeline<Sudoku>(operadores);

        SelectionStrategy<Object> selection = new RouletteWheelSelection();
        Random rng = new MersenneTwisterRNG();

        FitnessEvaluator<Sudoku> fit = new SudokuEvaluador(n);
        EvolutionEngine<Sudoku> engine = new GenerationalEvolutionEngine<Sudoku>(cf,
                pipeline,
                fit,
                selection,
                rng);

        Sudoku mejor = engine.evolve(POBLACION, 10, new GenerationCount(GENERACIONES));

        System.out.println("The best solution has a fitness value of "
                + fit.getFitness(mejor, null));
        System.out.println("Here's the completed Sudoku: ");
        Sudoku.Gen[] genes = mejor.cells;
        for (int k = 0; k < genes.length; k++) {
            int repr = genes[k].getValue();
            System.out.print(repr + "\t");
            if ((k + 1) % nn == 0) {
                System.out.println();
            }
        }

    }

    public static void main(String[] args) throws IOException {
        WatchmakerSudoku o=new WatchmakerSudoku();
        o.solveSudoku();
        // TODO code application logic here
    }
//    /**
//     * Trivial evolution observer for displaying information at the end
//     * of each generation.
//     */
//    private class EvolutionLogger implements EvolutionObserver<Sudoku> {
//
//        public void populationUpdate(PopulationData<Sudoku> data) {
//            sudokuView.setSolution(data.getBestCandidate());
//            generationsLabel.setText(String.valueOf(data.getGenerationNumber() + 1));
//            timeLabel.setText(TIME_FORMAT.format(((double) data.getElapsedTime()) / 1000));
//        }
//    }
}
