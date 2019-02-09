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
package module.graph.helper;

import java.io.Serializable;

public class WordToken implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8338067062131921969L;
	int startPos;
    int endPos;
    int index;
    public String pos;
    public String value;


    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public WordToken(int startPos, int endPos, int index, String pos,
                     String value) {
        super();
        this.startPos = startPos;
        this.endPos = endPos;
        this.index = index;
        this.pos = pos;
        this.value = value;
    }

    @Override
    public String toString() {
//		return String.format("%d \t %s/%s \t %s-%s",
//				index, value, pos, startPos, endPos );
        return String.format("%d \t %s \t %s",
                index, value, pos);
    }

}
