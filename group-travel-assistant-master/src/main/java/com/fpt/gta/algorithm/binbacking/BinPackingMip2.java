package com.fpt.gta.algorithm.binbacking;

//import com.skaggsm.ortools.OrToolsHelper;

public class BinPackingMip2 {
//    static {
//        System.loadLibrary("jniortools");
//        OrToolsHelper.loadLibrary();
//    }
//
//    static class DataModel {
//        public final double[] weights = {48, 30, 19, 36, 36, 27, 42, 42, 36, 24, 30};
//        public final int numItems = weights.length;
//        public final int numBins = weights.length;
//        public final int binCapacity = 100;
//    }
//
//    public static Map<Integer, List<Record>> run(List<Record> data, int binCapacity) throws Exception {
//
//        Map<Integer, List<Record>> map = new HashMap<>();
//        // Create the linear solver with the CBC backend.
//        MPSolver solver = new MPSolver(
//                "BinPackingMip2", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
//
//        MPVariable[][] x = new MPVariable[data.size()][data.size()];
//        for (int i = 0; i < data.size(); ++i) {
//            for (int j = 0; j < data.size(); ++j) {
//                x[i][j] = solver.makeIntVar(0, 1, "");
//            }
//        }
//        MPVariable[] y = new MPVariable[data.size()];
//        for (int j = 0; j < data.size(); ++j) {
//            y[j] = solver.makeIntVar(0, 1, "");
//        }
//
//        double infinity = Double.POSITIVE_INFINITY;
//        for (int i = 0; i < data.size(); ++i) {
//            MPConstraint constraint = solver.makeConstraint(1, 1, "");
//            for (int j = 0; j < data.size(); ++j) {
//                constraint.setCoefficient(x[i][j], 1);
//            }
//        }
//        // The bin capacity constraint for bin j is
//        //   sum_i w_i x_ij <= C*y_j
//        // To define this constraint, first subtract the left side from the right to get
//        //   0 <= C*y_j - sum_i w_i x_ij
//        //
//        // Note: Since sum_i w_i x_ij is positive (and y_j is 0 or 1), the right side must
//        // be less than or equal to C. But it's not necessary to add this constraint
//        // because it is forced by the other constraints.
//
//        for (int j = 0; j < data.size(); ++j) {
//            MPConstraint constraint = solver.makeConstraint(0, infinity, "");
//            constraint.setCoefficient(y[j], binCapacity);
//            for (int i = 0; i < data.size(); ++i) {
//                constraint.setCoefficient(x[i][j], -data.get(i).getTimeSpent());
//            }
//        }
//
//        MPObjective objective = solver.objective();
//        for (int j = 0; j < data.size(); ++j) {
//            objective.setCoefficient(y[j], 1);
//        }
//        objective.setMinimization();
//
//        final MPSolver.ResultStatus resultStatus = solver.solve();
//
//        // Check that the problem has an optimal solution.
//        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
//            System.out.println("Number of bins used: " + objective.value());
//            double totalWeight = 0;
//            for (int j = 0; j < data.size(); ++j) {
//                if (y[j].solutionValue() == 1) {
//                    System.out.println("\nBin " + j + "\n");
//                    map.put(j, new ArrayList<>());
//                    List<Record> list = map.get(j);
//                    double binWeight = 0;
//                    for (int i = 0; i < data.size(); ++i) {
//                        if (x[i][j].solutionValue() == 1) {
//                            list.add(data.get(i));
//                            map.put(j, list);
//                            System.out.println("Item " + data.get(i).getName() + " - weight: " + data.get(i).getTimeSpent());
//                            binWeight += data.get(i).getTimeSpent();
//                        }
//                    }
//                    System.out.println("Packed bin weight: " + binWeight);
//                    totalWeight += binWeight;
//                }
//            }
//            System.out.println("\nTotal packed weight: " + totalWeight);
//        } else {
//            System.err.println("The problem does not have an optimal solution.");
//        }
//        return map;
//    }
//
//    private BinPackingMip2() {
//    }
}
