package seneca.gui;

import org.apache.log4j.Logger;
import seneca.gui.actions.*;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;

public class SenecaComponentFactory {

    public static String senecaHome;
    private Map commands;
    private Map menuItems;
    private JMenuBar menubar;
    private JToolBar toolbar;
    private static ResourceBundle resources;
    public final static String imageSuffix = "Image";
    public final static String labelSuffix = "Label";
    public final static String actionSuffix = "Action";
    public final static String tipSuffix = "Tooltip";
    private static Logger logger = Logger.getLogger(SenecaComponentFactory.class);
    private Action[] defaultActions = {new NewAction(), new OpenAllAction(), new OpenPreviousAction(),
            new SaveAllAction(), new EditDataAction(), new ExitAction(), new AboutAction(), new CloseDataAction()};

    private SenecaComponentFactory() {
        setupActions();
        logger.info("Seneca components generated");
    }

    private static class InstanceHolder {

        private static final SenecaComponentFactory INSTANCE = new SenecaComponentFactory();
    }

    public static SenecaComponentFactory getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public ResourceBundle getResources() {
        return resources;
    }

    protected JMenuItem getMenuItem(String cmd) {
        return (JMenuItem) menuItems.get(cmd);
    }

    private void setupActions() {
        commands = new HashMap();
        Action[] actions = getActions();
        for (int i = 0; i < actions.length; i++) {
            Action a = actions[i];
            commands.put(a.getValue(Action.NAME), a);
        }
    }

    public Action[] getActions() {
        return defaultActions;
    }

    protected Action getAction(String cmd) {
        return (Action) commands.get(cmd);
    }

    /**
     * This method takes a string and checks in the Seneca.properties file if this string is
     * defined as an attribute and retrieves its value
     *
     * @param nm
     * @return
     */
    public String getResourceString(String nm) {
        String str;
        try {
            str = resources.getString(nm);
        } catch (MissingResourceException mre) {
            str = null;
        }
        return str;
    }

    public Component createToolbar() {
        toolbar = new JToolBar();
        String[] toolKeys = tokenize(getResourceString("toolbar"));
        for (int i = 0; i < toolKeys.length; i++) {
            if (toolKeys[i].equals("-")) {
                toolbar.add(Box.createHorizontalStrut(5));
            } else {
                toolbar.add(createTool(toolKeys[i]));
            }
        }
        toolbar.add(Box.createHorizontalGlue());
        return toolbar;
    }

    /**
     * This method takes a key and it will get the url associated with that key by translating
     * that key into an url, using the definitions in the Seneca.properties file.
     *
     * @param key
     * @return
     */
    protected URL getResource(String key) {
        String name = getResourceString(key);
        if (name != null) {
            URL url = this.getClass().getResource(name);
            return url;
        }
        return null;
    }

    protected Container getToolbar() {
        return toolbar;
    }

    protected JMenuBar getMenubar() {
        return menubar;
    }

    protected JMenuItem createMenuItem(String cmd) {
        JMenuItem mi = new JMenuItem(getResourceString(cmd + labelSuffix));
        URL url = getResource(cmd + imageSuffix);
        //logger.debug("Using imageSuffix " + cmd + imageSuffix);
        if (url != null) {
            mi.setHorizontalTextPosition(SwingConstants.RIGHT);
            mi.setIcon(new ImageIcon(url));
        }
        String astr = getResourceString(cmd + actionSuffix);
        if (astr == null) {
            astr = cmd;
        }
        mi.setActionCommand(astr);
        Action a = getAction(astr);
        if (a != null) {
            mi.addActionListener(a);
        } else {
            mi.setEnabled(false);
        }
        menuItems.put(cmd, mi);
        return mi;
    }

    protected Component createTool(String key) {
        return createToolbarButton(key);
    }

    protected JButton createToolbarButton(String key) {
        String s = key + imageSuffix;

        URL url = getResource(s);
//        InputStream image = this.getClass().getResourceAsStream(s);
//        System.out.println(key+"\n"+s+"\n"+url);
        JButton b = new JButton(new ImageIcon(url)) {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            
            public float getAlignmentY() {
                return 0.5f;
            }
        };
        b.setRequestFocusEnabled(false);
        b.setMargin(new Insets(1, 1, 1, 1));
        String astr = getResourceString(key + actionSuffix);
        if (astr == null) {
            astr = key;
        }

        Action a = getAction(astr);
        if (a != null) {
            b.setActionCommand(astr);
            b.addActionListener(a);
        } else {
            b.setEnabled(false);
        }
        String tip = getResourceString(key + tipSuffix);
        if (tip != null) {
            b.setToolTipText(tip);
        }
        return b;
    }

    protected String[] tokenize(String input) {
        List v = new ArrayList();
        StringTokenizer t = new StringTokenizer(input);
        String cmd[];
        while (t.hasMoreTokens()) {
            v.add(t.nextToken());
        }
        cmd = new String[v.size()];
        for (int i = 0; i < cmd.length; i++) {
            cmd[i] = (String) v.get(i);
        }
        return cmd;
    }

    protected JMenuBar createMenubar() {
        JMenuBar mb = new JMenuBar();
        menuItems = new HashMap();
        String[] menuKeys = tokenize(getResourceString("menubar"));
        for (int i = 0; i < menuKeys.length; i++) {
            JMenu m = createMenu(menuKeys[i]);
            //System.out.println(m.getComponent().getName());
            if (m != null) {
                mb.add(m);
            }
        }
        return mb;
    }

    protected JMenu createMenu(String key) {
        String[] itemKeys = tokenize(getResourceString(key));
        String menuName = getResourceString(key + "Label");
        JMenu menu = new JMenu(menuName);
        for (int i = 0; i < itemKeys.length; i++) {
            if (itemKeys[i].equals("-")) {
                menu.addSeparator();
            } else if (itemKeys[i].endsWith("}")) {
                String menuTitle = itemKeys[i].substring(1,
                        itemKeys[i].length() - 1);
                JMenu me = createMenu(menuTitle);
                menu.add(me);
            } else {
                JMenuItem mi = createMenuItem(itemKeys[i]);
                menu.add(mi);
            }
        }
        return menu;
    }

    protected void disableDefault() {
        String[] itemsToDisable = tokenize(getResourceString("disable"));
        String ident;
        for (int f = 0; f < defaultActions.length; f++) {
            ident = (String) defaultActions[f].getValue(Action.NAME);
            for (int i = 0; i < itemsToDisable.length; i++) {
                if (ident.equals(itemsToDisable[i])) {
                    defaultActions[f].setEnabled(false);
                }
            }
        }
    }

    static {
        try {
//			resources = ResourceBundle.getBundle("seneca.gui.Seneca",
//					Locale.getDefault());//
            resources = ResourceBundle.getBundle("properties/Seneca",
                    Locale.getDefault());
            senecaHome = System.getProperty("seneca.home");
        } catch (MissingResourceException mre) {
            System.err.println("Seneca.properties not found");
            logger.error("Seneca.properties not found");
            System.exit(1);
        }
    }
}
