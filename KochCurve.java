import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Polygon;

class KochCurve extends JFrame{

    // inner class to represent the vectors used for building the Koch curve
    private static class MyVector{
        public final double x;
        public final double y;

        public MyVector(double x, double y){
            this.x = x;
            this.y = y;
        }

        public double length(){
            return Math.sqrt(x*x + y*y);
        }

        public MyVector normalized(){
            double len = length();
            return new MyVector(x/len, y/len);
        }

        public MyVector subtract(MyVector other){
            return new MyVector(x-other.x, y-other.y);
        }

        public MyVector plus(MyVector other){
            return new MyVector(x+other.x, y+other.y);
        }

        // scalar multiplication
        public MyVector multiply(double s){   
            return new MyVector(s*x, s*y);
        }

        public MyVector linearInterpolation(MyVector other, double t){
            return new MyVector((1-t)*x + t*other.x, (1-t)*y + t*other.y);
        }

        public String toString(){
            return "(" + x + ", " + y + ")";
        }
    }

    /* constructor of class Kochkurve creates the JPanel with the Koch curve for given
    * starting points and number of recursive steps and adds it to the JFrame
    */
    public KochCurve(int[] poly_xPoints, int[] poly_yPoints, int steps){
        var panel = new JPanel(){
            @Override protected void paintComponent(Graphics g) {
                var polygon = new Polygon(poly_xPoints, poly_yPoints, poly_xPoints.length);
                g.drawPolygon(subdevide(polygon, steps));
            }
        };

        add(panel);
        pack();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(500,500);
        setVisible(true);
    }

    // subdevide each section of the given polygon-curve into 4 Koch curve sections
    static Polygon subdevideIntoFourSections(Polygon polygon){
        int npoints = polygon.npoints;
        int xpoints[] = polygon.xpoints;
        int ypoints[] = polygon.ypoints;
        var kochedPolygon = new Polygon();
        
        for(int i = 0; i < npoints; i++){
            int iplus1 = (i+1) % npoints;
            var vecA = new MyVector(xpoints[i], ypoints[i]);
            var vecE = new MyVector(xpoints[iplus1], ypoints[iplus1]);
            kochedPolygon.addPoint((int)vecA.x, (int)vecA.y);
            
            var vecB = vecE.linearInterpolation(vecA, (double)2/3);
            kochedPolygon.addPoint((int)vecB.x, (int)vecB.y);

            var vecM = vecA.linearInterpolation(vecE, 0.5);
            var vecAE = vecE.subtract(vecA);
            var vecn = new MyVector(vecAE.y, -1.0*vecAE.x).normalized();
            double length_e = Math.sqrt(vecAE.length() * vecAE.length()/ 12.0);
            var vecC = vecM.plus(vecn.multiply(length_e));
            kochedPolygon.addPoint((int)vecC.x, (int)vecC.y);

            var vecD = vecE.linearInterpolation(vecA, (double) 1/3);
            kochedPolygon.addPoint((int)vecD.x, (int)vecD.y);
        }
        return kochedPolygon;
    }

    // transform the given polygon-curve "steps"-times by calling the subdevide(Polygon polygon) method recursively
    static Polygon subdevide(Polygon polygon, int steps){
        if (steps == 0) return polygon;
        return subdevide(subdevideIntoFourSections(polygon), steps-1);
    }

    public static void main(String args[]){
        // starting points for triangle
        int[] poly_xPoints = {250, 400, 100};
        int[] poly_yPoints = {50, 350, 350};
        int nsteps = 4; // default value
        String usageText =  "    Usage: KochCurve nsteps\n\n    parameters:\n"
          + "        nsteps: number of steps the Koch cuve is to be transformed (max. 5)\n";

        if(args.length == 0){
            System.out.println(String.format("""
                \n... no command line parameter for the number of transformation steps
                was specified, therefore the default value of %d is used.\n
            """, nsteps));
            System.out.println(usageText);   
        } else{
            try{
                nsteps = Integer.valueOf(args[0]);
                if(nsteps > 5) {
                    nsteps = 4; 
                    throw new Exception("... maximum value of \"nsteps\" exceeded.");
                }
            } catch(Exception e) {
                System.err.println(e);
                System.out.println(String.format("""
                    \n... wrong format for the command line parameter \"nsteps\" is given,
                    an integer value (< 6) is mandatory. Due to conversion error, 
                    the default value of %d is used.
                """, nsteps));
                System.out.println(usageText);
            }
        }
        new KochCurve(poly_xPoints, poly_yPoints, nsteps).setTitle(String.format("Aufgabe4: Koch-Kurve der Stufe %d", nsteps));
    }
}