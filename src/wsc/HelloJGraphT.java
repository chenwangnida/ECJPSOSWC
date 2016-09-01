package wsc;
/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */

/* -----------------
 * HelloJGraphT.java
 * -----------------
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh
 * Contributor(s):   -
 *
 * $Id$
 *
 * Changes
 * -------
 * 27-Jul-2003 : Initial revision (BN);
 *
 */

import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.graph.*;

/**
 * A simple introduction to using JGraphT.
 *
 * @author Barak Naveh
 * @since Jul 27, 2003
 */
public final class HelloJGraphT {
	private HelloJGraphT() {
	} // ensure non-instantiability.

	/**
	 * The starting point for the demo.
	 *
	 * @param args
	 *            ignored.
	 */
	public static void main(String[] args) {
		DirectedGraph<String, DefaultEdge> stringGraph = createStringGraph();

		System.out.println("graph printing " + stringGraph.toString());
		Set<DefaultEdge> allEdgefromOnetoAntoher = stringGraph.getAllEdges("v1", "v2");
		Set<DefaultEdge> allEdge = stringGraph.edgeSet();
		Set<String> allVertice = stringGraph.vertexSet();

		List<String> dangleVerticeList = new ArrayList<String>();
		String dangleVertice;
		for (String v : allVertice) {
			System.out.println("vertice iterator:" + v.toString());
			int relatedOutDegree = stringGraph.outDegreeOf(v);
			System.out.println("degree is :" + relatedOutDegree);

			if (relatedOutDegree == 0 && !v.equals("v5")) {
				dangleVerticeList.add(v);
				System.out.println("dangleVertice:" + dangleVerticeList.get(0));
			}
		}

		// recursion for find end tangle, remove them and update graph
		removeAlltangle(stringGraph, dangleVerticeList);

		System.out.println(stringGraph.toString());

	}

	public static void removeAlltangle(DirectedGraph<String, DefaultEdge> stringGraph, List<String> dangleVerticeList) {
		// Iterator the endTangle
		for (String danglevertice : dangleVerticeList) {

			Set<DefaultEdge> relatedEdge = stringGraph.incomingEdgesOf(danglevertice);

			for (DefaultEdge edge : relatedEdge) {
				String potentialTangleVertice = stringGraph.getEdgeSource(edge);

				System.out.println("potentialTangleVertice:" + potentialTangleVertice);
			}

			Set<DefaultEdge> ralatedEdgeSave = new HashSet<DefaultEdge>();
			ralatedEdgeSave.addAll(relatedEdge);

			stringGraph.removeVertex(danglevertice);

			for (DefaultEdge edge : ralatedEdgeSave) {
				String potentialTangleVertice = stringGraph.getEdgeSource(edge);
				int relatedOutDegree = stringGraph.outDegreeOf(potentialTangleVertice);

				System.out.println(
						"potentialTangleVertice:" + potentialTangleVertice + ",updateOutDegree:" + relatedOutDegree);
				List<String> dangleVerticeList1 = new ArrayList<String>();
				if (relatedOutDegree == 0) {
					dangleVerticeList1.add(potentialTangleVertice);
					removeAlltangle(stringGraph, dangleVerticeList1);
				} else {
					return;
				}

			}

		}
	}

	/**
	 * Creates a toy directed graph based on URL objects that represents link
	 * structure.
	 *
	 * @return a graph based on URL objects.
	 */
	private static DirectedGraph<URL, DefaultEdge> createHrefGraph() {
		DirectedGraph<URL, DefaultEdge> g = new DefaultDirectedGraph<URL, DefaultEdge>(DefaultEdge.class);

		try {
			URL amazon = new URL("http://www.amazon.com");
			URL yahoo = new URL("http://www.yahoo.com");
			URL ebay = new URL("http://www.ebay.com");

			// add the vertices
			g.addVertex(amazon);
			g.addVertex(yahoo);
			g.addVertex(ebay);

			// add edges to create linking structure
			g.addEdge(yahoo, amazon);
			g.addEdge(yahoo, ebay);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return g;
	}

	/**
	 * Create a toy graph based on String objects.
	 *
	 * @return a graph based on String objects.
	 */
	private static DirectedGraph<String, DefaultEdge> createStringGraph() {
		DirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

		String v1 = "v1";
		String v2 = "v2";
		String v3 = "v3";
		String v4 = "v4";
		String v5 = "v5";
		String v6 = "v6";
		String v7 = "v7";
		String v8 = "v8";
		String v9 = "v9";
		String v10 = "v10";
		String v11 = "v11";

		// add the vertices
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		g.addVertex(v5);
		g.addVertex(v6);
		g.addVertex(v7);
		g.addVertex(v8);
		g.addVertex(v9);
		g.addVertex(v10);
		g.addVertex(v11);



		// add edges to create a circuit
		g.addEdge(v1, v2);
		g.addEdge(v2, v3);
		g.addEdge(v3, v6);
		g.addEdge(v6, v7);
		g.addEdge(v6, v8);
		g.addEdge(v7, v9);
		g.addEdge(v8, v9);
		g.addEdge(v3, v4);
		g.addEdge(v4, v5);
		g.addEdge(v3, v10);
		g.addEdge(v10, v11);

		return g;
	}
}

// End HelloJGraphT.java
