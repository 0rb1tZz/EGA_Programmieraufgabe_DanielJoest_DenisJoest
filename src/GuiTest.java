import graph.Graph1;
import gui.StaticGraphGUI;

void main() {
    Graph1<String> graph = new Graph1<>(); // initially empty
    StaticGraphGUI<String> gui = new StaticGraphGUI<>(graph);

    gui.addContextMenu(new StaticGraphGUI.ContextAction("test", (mouseEvent, node) -> {
        System.out.println("test");
    }));
}