/*******************************************************************************
 * Copyright (C) 2017 Arpit Sharma
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package module.graph.pdtbp.utils;

public class Result {

	public double prec;
	public double recall;
	public double f1;
	public double acc;
	public int tp;
	public int fp;
	public int fn;
	public int tn;

	public static Result calcResults(double gsTotal, double prdTotal, int correct) {

		double p = prdTotal == 0 ? 0 : (1.0 * correct / prdTotal) * 100;
		double r = gsTotal == 0 ? 0 : (1.0 * correct / gsTotal) * 100;
		double f1 = (2 * p * r) / (r + p);

		return new Result(p, r, f1, -1);
	}

	Result(double p, double r, double f1, double acc) {
		prec = p;
		recall = r;
		this.f1 = f1;
		this.acc = acc;
	}

	public String print(double num) {
		return String.format("%.2f", num);
	}

	public String printAll() {
		return "\n\nPrec\tRecall\tF1\n" + print(prec) + "\t" + print(recall) + "\t" + print(f1)+"\n";
	}
}
