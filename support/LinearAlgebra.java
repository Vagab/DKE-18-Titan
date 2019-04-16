package support;

import java.lang.Math;
import support.Vector;

public class LinearAlgebra {

  // takes vector and linear transformation(matrix) as parameters, returns changed vector
  public static Vector rotation(Vector v, double[][] m) {
    double[] res = new double[3];
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
          res[i] += m[i][j] * v.coords()[j];
      }
    }
    Vector vector = new Vector(res[0], res[1], res[2]);
    return vector;
  }
  //instantiate X rotating linear transformation
  public static double[][] instRotTrX(double angle) {
    double[][] mat = {
                    {1, 0, 0},
                    {0, Math.cos(angle), -Math.sin(angle)},
                    {0, Math.sin(angle), Math.cos(angle)}
                  };
    return mat;
  }
  //instantiate Y rotating linear transformation
  public static double[][] instRotTrY(double angle) {
    double[][] mat = {
                    {Math.cos(angle), 0, Math.sin(angle)},
                    {0, 1, 0},
                    {-Math.sin(angle), 0, Math.cos(angle)}
                  };
    return mat;
  }
  //instantiate Z rotating linear transformation
  public static double[][] instRotTrZ(double angle) {
    double[][] mat = {
                    {Math.cos(angle), -Math.sin(angle), 0},
                    {Math.sin(angle), Math.cos(angle), 0},
                    {0, 0, 1}
                  };
    return mat;
  }

  public static void main(String[] args) {
    Vector vector = new Vector(1, 1, 1);
    Vector res;
    double angle = Double.parseDouble(args[1]);
    double option = Double.parseDouble(args[0]);
    System.out.println(angle);
    if(option == 1.0) {
      System.out.println("X rotation");
      res = rotation(vector, instRotTrX(angle));
    } else if (option == 2.0) {
      System.out.println("Y rotation");
      res = rotation(vector, instRotTrY(angle));
    } else {
      System.out.println("Z rotation");
      res = rotation(vector, instRotTrZ(angle));
    }
    System.out.println(res.toString());
  }
}
