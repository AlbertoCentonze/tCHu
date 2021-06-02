package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerAIMedium extends PlayerAI {
    private static final int PROBABILITY_DRAW_TICKET = 5;
    private static final int MEDIUM_NUMBER_OF_POINTS = 10;

    private final List<Ticket> ticketsToBuild = new ArrayList<>();

    /**
     * Constructor
     * @param seed can be null
     */
    public PlayerAIMedium(Integer seed) {
        super(seed);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        // keep Ticket with the least points, the most points and the middle number of points
        SortedBag<Ticket> options = initialTickets;
        List<Ticket> orderedList = options.toList();
        orderedList.sort(Comparator.comparingInt(Ticket::points));
        for (int i = 1; i <= 3; i += 2)
            orderedList.remove(i); // removes at slot 1 and 3
        SortedBag<Ticket> chosen = SortedBag.of(orderedList);
        ticketsToBuild.addAll(chosen.toList());
        return chosen;
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        List<Integer> points = options.stream().mapToInt(Ticket::points).sorted()
                .boxed().collect(Collectors.toList());
        SortedBag<Ticket> chosen;

        if(ticketsToBuild.size() == 0) { // choose tickets with the most and least points
            points.remove(1);
            chosen = SortedBag.of(options.stream().filter(t -> points.contains(t.points()))
                    .collect(Collectors.toList()));
        }
        else if(ticketsToBuild.get(0).points() > MEDIUM_NUMBER_OF_POINTS) {
            // choose ticket with the least points
            chosen = SortedBag.of(options.stream().filter(t -> points.get(0) == t.points())
                    .collect(Collectors.toList()));
        } else {
            // choose ticket with the most points
            chosen = SortedBag.of(options.stream().filter(t -> points.get(Constants.IN_GAME_TICKETS_COUNT - 1) == t.points())
                    .collect(Collectors.toList()));
        }
        for(Ticket t : chosen) {
            if(t.points(ownState.connections()) < 0) {
                ticketsToBuild.add(t);
            }
        }
        return chosen;
    }

    @Override
    public int drawSlot() {
        // prioritize locomotives
        if(gameState.cardState().faceUpCards().contains(Card.LOCOMOTIVE)) {
            return gameState.cardState().faceUpCards().indexOf(Card.LOCOMOTIVE);
        } // otherwise choose randomly
        /*SortedBag<Card> s = SortedBag.of(gameState.cardState().faceUpCards());
        OptionalInt max = Card.ALL.stream().mapToInt(s::countOf).max();
        for(Card c : Card.ALL) {
            if(s.countOf(c) == max.getAsInt()) {
                return
            }
        }*/
        return super.drawSlot();
    }

    @Override
    public TurnKind nextTurn() {
        // remove a ticket from the tickets to build if it has been fully built
        ownState.tickets().forEach(t -> {
            if(ticketsToBuild.contains(t) && t.points(ownState.connections()) > 0) {
                ticketsToBuild.remove(t);
            }
        });
        // drawing tickets if all tickets have been built
        // or if 1 ticket is left to build with a probability of 0.2
        if(gameState.canDrawTickets() && (ticketsToBuild.size() == 0 ||
                (ticketsToBuild.size() == 1 && rng.nextInt(PROBABILITY_DRAW_TICKET) == 0))) {
            return TurnKind.DRAW_TICKETS;
        }
        updateClaimable();
        if(!claimable.isEmpty()) {
            // TODO prioritize routes that are connected
            routeToClaim = claimable.get(rng.nextInt(claimable.size()));
            return TurnKind.CLAIM_ROUTE;
        } else if(gameState.canDrawCards()) { // otherwise draws card
            return TurnKind.DRAW_CARDS;
        } else {
            return TurnKind.DRAW_TICKETS; // TODO default value
        }
    }








    
    public List<List<Station>> shortestTrip(Ticket ticket) { // TODO issue for tickets with multiple trips
        GraphWeighted graphWeighted = createdWeightedGraph();
        List<List<Station>> s = new ArrayList<>();
        for(Trip t : ticket.trips()) { // if multiple trips, it calculates the shortest path for each trip
            s.add(graphWeighted.DijkstraShortestPath(mapOfNodes.get(ticket.trips().get(0).from().id()),
                    mapOfNodes.get(ticket.trips().get(0).to().id())));
            graphWeighted.resetNodesVisited();
        }
        return s;
    }





    private final Map<Integer,NodeWeighted> mapOfNodes = createMapOfNodes();
    private Map<Integer,NodeWeighted> createMapOfNodes() {
        Map<Integer,NodeWeighted> map = new HashMap<>();
        for(Station s : ChMap.stations()) {
            map.put(s.id(), new NodeWeighted(s, s.name()));
        }
        return map;
    }

    private GraphWeighted createdWeightedGraph() {
        GraphWeighted graphWeighted = new GraphWeighted(true);
        ChMap.routes().forEach(r -> graphWeighted.addEdge(mapOfNodes.get(r.station1().id()),
                mapOfNodes.get(r.station2().id()), r.length()));
        assignEdges();
        return graphWeighted;
    }


    private final List<EdgeWeighted> listEdges = ChMap.routes().stream().map(r ->
            new EdgeWeighted(mapOfNodes.get(r.station1().id()), mapOfNodes.get(r.station2().id()), r.length()))
            .collect(Collectors.toList());

    private void assignEdges() {
        for(EdgeWeighted edge : listEdges) {
            edge.departure.edges.add(edge);
            edge.destination.edges.add(edge);
        }
    }


    public class EdgeWeighted implements Comparable<EdgeWeighted> { // TODO it's just a Route
        NodeWeighted departure;
        NodeWeighted destination;
        int weight;

        EdgeWeighted(NodeWeighted dep, NodeWeighted des, int w) {
            departure = dep;
            destination = des;
            weight = w;
        }

        public String toString() {
            return String.format("(%s -> %s, %d)", departure.name, destination.name, weight);
        }

        // We need this method if we want to use PriorityQueues instead of LinkedLists
        // to store our edges, the benefits are discussed later, we'll be using LinkedLists
        // to make things as simple as possible
        public int compareTo(EdgeWeighted otherEdge) {
            if (this.weight > otherEdge.weight) {
                return 1;
            }
            else return -1;
        }
    }


    public class NodeWeighted {
        Station s;
        String name;
        private boolean visited;
        LinkedList<EdgeWeighted> edges;

        NodeWeighted(Station s, String name) {
            this.s = s;
            this.name = name;
            visited = false;
            edges = new LinkedList<>();
        }

        boolean isVisited() {
            return visited;
        }

        void visit() {
            visited = true;
        }

        void unvisit() {
            visited = false;
        }
    }


    public class GraphWeighted {
        private Set<NodeWeighted> nodes;
        private boolean directed;

        GraphWeighted(boolean directed) {
            this.directed = directed;
            nodes = new HashSet<>();
        }

        // Doesn't need to be called for any node that has an edge to another node
        // since addEdge makes sure that both nodes are in the nodes Set
        /*public void addNode(NodeWeighted... n) {
            // We're using a var arg method so we don't have to call
            // addNode repeatedly
            nodes.addAll(Arrays.asList(n));
        }*/

        public void addEdge(NodeWeighted source, NodeWeighted destination, int weight) {
            // Since we're using a Set, it will only add the nodes
            // if they don't already exist in our graph
            nodes.add(source);
            nodes.add(destination);

            // We're using addEdgeHelper to make sure we don't have duplicate edges
            addEdgeHelper(source, destination, weight);

            if (!directed && source != destination) {
                addEdgeHelper(destination, source, weight);
            }
        }

        private void addEdgeHelper(NodeWeighted a, NodeWeighted b, int weight) {
            // Go through all the edges and see whether that edge has
            // already been added
            for (EdgeWeighted edge : a.edges) {
                if (edge.departure == a && edge.destination == b) {
                    // Update the value in case it's a different one now
                    edge.weight = weight;
                    return;
                }
            }
            // If it hasn't been added already (we haven't returned
            // from the for loop), add the edge
            a.edges.add(new EdgeWeighted(a, b, weight));
        }

        /*public void printEdges() {
            for (NodeWeighted node : nodes) {
                LinkedList<EdgeWeighted> edges = node.edges;

                if (edges.isEmpty()) {
                    System.out.println("Node " + node.name + " has no edges.");
                    continue;
                }
                System.out.print("Node " + node.name + " has edges to: ");

                for (EdgeWeighted edge : edges) {
                    System.out.print(edge.destination.name + "(" + edge.weight + ") ");
                }
                System.out.println();
            }
        }*/

        public boolean hasEdge(NodeWeighted source, NodeWeighted destination) {
            LinkedList<EdgeWeighted> edges = source.edges;
            for (EdgeWeighted edge : edges) {
                // Again relying on the fact that all classes share the
                // exact same NodeWeighted object
                if (edge.destination == destination) {
                    return true;
                }
            }
            return false;
        }

        // Necessary call if we want to run the algorithm multiple times
        public void resetNodesVisited() {
            for (NodeWeighted node : nodes) {
                node.unvisit();
            }
        }

        public List<Station> DijkstraShortestPath(NodeWeighted start, NodeWeighted end) {
            // We keep track of which path gives us the shortest path for each node
            // by keeping track how we arrived at a particular node, we effectively
            // keep a "pointer" to the parent node of each node, and we follow that
            // path to the start
            HashMap<NodeWeighted, NodeWeighted> changedAt = new HashMap<>();
            changedAt.put(start, null);

            // Keeps track of the shortest path we've found so far for every node
            HashMap<NodeWeighted, Double> shortestPathMap = new HashMap<>();

            // Setting every node's shortest path weight to positive infinity to start
            // except the starting node, whose shortest path weight is 0
            for (NodeWeighted node : nodes) {
                if (node == start)
                    shortestPathMap.put(start, 0.0);
                else shortestPathMap.put(node, Double.POSITIVE_INFINITY);
            }

            // Now we go through all the nodes we can go to from the starting node
            // (this keeps the loop a bit simpler)
            for (EdgeWeighted edge : start.edges) {
                shortestPathMap.put(edge.destination, (double) edge.weight);
                changedAt.put(edge.destination, start);
            }

            start.visit();

            // This loop runs as long as there is an unvisited node that we can
            // reach from any of the nodes we could till then
            while (true) {
                List<Station> pathStations = new LinkedList<>();
                NodeWeighted currentNode = closestReachableUnvisited(shortestPathMap);
                // If we haven't reached the end node yet, and there isn't another
                // reachable node the path between start and end doesn't exist
                // (they aren't connected)
                if (currentNode == null) { // TODO never possible
                    System.out.println("There isn't a path between " + start.name + " and " + end.name);
                    return List.of();
                }

                // If the closest non-visited node is our destination, we want to print the path
                if (currentNode == end) {
                    System.out.println("The path with the smallest weight between "
                            + start.name + " and " + end.name + " is:");

                    NodeWeighted child = end;

                    // It makes no sense to use StringBuilder, since
                    // repeatedly adding to the beginning of the string
                    // defeats the purpose of using StringBuilder
                    String path = end.name;
                    pathStations.add(end.s);

                    while (true) {
                        NodeWeighted parent = changedAt.get(child);
                        if (parent == null) {
                            break;
                        }

                        // Since our changedAt map keeps track of child -> parent relations
                        // in order to print the path we need to add the parent before the child and
                        // it's descendants
                        path = parent.name + " " + path;

                        pathStations.add(0, parent.s); // TODO or need LinkedList to add parent at the beginning
                        child = parent;
                    }
                    System.out.println(path);
                    System.out.println("The path costs: " + shortestPathMap.get(end));
                    return pathStations;
                }
                currentNode.visit();

                // Now we go through all the unvisited nodes our current node has an edge to
                // and check whether its shortest path value is better when going through our
                // current node than whatever we had before
                for (EdgeWeighted edge : currentNode.edges) {
                    if (edge.destination.isVisited())
                        continue;

                    if (shortestPathMap.get(currentNode)
                            + edge.weight
                            < shortestPathMap.get(edge.destination)) {
                        shortestPathMap.put(edge.destination,
                                shortestPathMap.get(currentNode) + edge.weight);
                        changedAt.put(edge.destination, currentNode);
                    }
                } // TODO don't understand
            }
        }

        private NodeWeighted closestReachableUnvisited(HashMap<NodeWeighted, Double> shortestPathMap) {

            double shortestDistance = Double.POSITIVE_INFINITY;
            NodeWeighted closestReachableNode = null;
            for (NodeWeighted node : nodes) {
                if (node.isVisited())
                    continue;

                double currentDistance = shortestPathMap.get(node);
                if (currentDistance == Double.POSITIVE_INFINITY)
                    continue;

                if (currentDistance < shortestDistance) {
                    shortestDistance = currentDistance;
                    closestReachableNode = node;
                }
            }
            System.out.println(closestReachableNode == null);
            if(closestReachableNode != null) {
                System.out.println(closestReachableNode.name);
            }
            return closestReachableNode;
        }
    }
}

/* hard:
takes cards that could be helpful to opponent -> opponent needs a card to claim a route
to connect two other routes of his -> if that card is in the faceUpCards the AI takes it
claims routes that link two routes of the opponent
prioritizes double routes (helpful?)
when choosing tickets takes tickets for which he has already built many routes
and for which the opponent hasn't built many routes

prioritize tunnels?
prioritize longest? & play against opponent for longest?

what is a smart way to choose the initial claim cards and the additional cards?
    additional cards: if they're necessary to claim another more important route, then NOT claim the tunnel
 */
