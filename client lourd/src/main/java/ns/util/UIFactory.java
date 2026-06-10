package ns.util;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class UIFactory {

    private UIFactory() {}

    public static JButton btnPrimary(String t) { return btn(t, new Color(0x1a3a6e), Style.C_TEXT); }
    public static JButton btnSuccess(String t)  { return btn(t, new Color(0x14532d), new Color(0x86efac)); }
    public static JButton btnDanger(String t)   { return btn(t, new Color(0x7f1d1d), new Color(0xfca5a5)); }
    public static JButton btnWarning(String t)  { return btn(t, new Color(0x78350f), new Color(0xfcd34d)); }

    public static JButton btnOutline(String t) {
        JButton b = new JButton(t);
        b.setFont(Style.FONT_BOLD);
        b.setForeground(Style.C_TEXT);          // texte blanc
        b.setBackground(new Color(0x111d33));   // fond dark
        b.setOpaque(true);
        b.setBorder(new LineBorder(Style.C_BORDER_HI, 1, true));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(130, 32));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(new Color(0x1e3a6e)); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(new Color(0x111d33)); }
        });
        return b;
    }

    private static JButton btn(String t, Color bg, Color fg) {
        JButton b = new JButton(t);
        b.setFont(Style.FONT_BOLD);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(140, 32));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(bg.brighter()); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    public static JTextField field(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(Style.FONT_NORMAL);
        tf.setBackground(Style.BG_INPUT);
        tf.setForeground(Style.C_TEXT);
        tf.setCaretColor(Style.C_ICE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Style.C_BORDER_HI, 1, true),
            BorderFactory.createEmptyBorder(5, 9, 5, 9)
        ));
        return tf;
    }

    public static JPasswordField passField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setFont(Style.FONT_NORMAL);
        pf.setBackground(Style.BG_INPUT);
        pf.setForeground(Style.C_TEXT);
        pf.setCaretColor(Style.C_ICE);
        pf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Style.C_BORDER_HI, 1, true),
            BorderFactory.createEmptyBorder(5, 9, 5, 9)
        ));
        return pf;
    }

    public static JTextArea textArea(int rows, int cols) {
        JTextArea ta = new JTextArea(rows, cols);
        ta.setFont(Style.FONT_NORMAL);
        ta.setBackground(Style.BG_INPUT);
        ta.setForeground(Style.C_TEXT);
        ta.setCaretColor(Style.C_ICE);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(BorderFactory.createEmptyBorder(5, 9, 5, 9));
        return ta;
    }

    public static <T> JComboBox<T> combo() {
        JComboBox<T> cb = new JComboBox<>();
        cb.setFont(Style.FONT_NORMAL);
        cb.setBackground(Style.BG_INPUT);
        cb.setForeground(Style.C_TEXT);
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v,
                    int i, boolean sel, boolean foc) {
                JLabel c = (JLabel) super.getListCellRendererComponent(l, v, i, sel, foc);
                c.setBackground(sel ? Style.C_SELECT : Style.BG_INPUT);
                c.setForeground(Style.C_TEXT);
                c.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
                return c;
            }
        });
        return cb;
    }

    public static JLabel labelTitre(String t) {
        JLabel l = new JLabel(t);
        l.setFont(Style.FONT_TITLE);
        l.setForeground(Style.C_TEXT);
        return l;
    }

    public static JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setFont(Style.FONT_BOLD);
        l.setForeground(Style.C_TEXT_DIM);
        return l;
    }

    public static JPanel groupPanel(String titre) {
        JPanel p = new JPanel();
        p.setBackground(Style.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                new LineBorder(Style.C_BORDER_HI, 1, true), " " + titre + " ",
                TitledBorder.LEFT, TitledBorder.TOP,
                Style.FONT_BOLD, Style.C_ICE),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return p;
    }

    // ── Tableau 100% dark — aucun blanc ──────────────────────────────────────
    public static void styleTable(JTable table) {
        // Forcer le LAF à ne pas injecter de couleurs système
        UIManager.put("Table.background",          Style.BG_CARD);
        UIManager.put("Table.foreground",          Style.C_TEXT);
        UIManager.put("Table.selectionBackground", Style.C_SELECT);
        UIManager.put("Table.selectionForeground", Style.C_TEXT);
        UIManager.put("Table.gridColor",           Style.C_BORDER);
        UIManager.put("TableHeader.background",    Style.C_TABLE_HEADER);
        UIManager.put("TableHeader.foreground",    Style.C_TEXT);
        UIManager.put("TableHeader.cellBorder",    BorderFactory.createMatteBorder(0,0,1,1, Style.C_BORDER));

        table.setFont(Style.FONT_NORMAL);
        table.setRowHeight(Style.ROW_HEIGHT);
        table.setBackground(Style.BG_CARD);
        table.setForeground(Style.C_TEXT);
        table.setGridColor(Style.C_BORDER);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(Style.C_SELECT);
        table.setSelectionForeground(Style.C_TEXT);
        table.setFillsViewportHeight(true);
        table.setOpaque(true);

        // Header : fond très sombre, texte blanc
        JTableHeader header = table.getTableHeader();
        header.setFont(Style.FONT_BOLD);
        header.setBackground(Style.C_TABLE_HEADER);     // 0x0f1f3d
        header.setForeground(Style.C_TEXT);             // blanc cassé
        header.setOpaque(true);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
        // Renderer du header pour garantir les couleurs
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                c.setBackground(Style.C_TABLE_HEADER);
                c.setForeground(Style.C_TEXT);
                c.setFont(Style.FONT_BOLD);
                c.setHorizontalAlignment(SwingConstants.LEFT);
                c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 1, Style.C_BORDER),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));
                c.setOpaque(true);
                return c;
            }
        });

        // Renderer des cellules : alternance dark
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? Style.C_ROW_EVEN : Style.C_ROW_ODD);
                    c.setForeground(Style.C_TEXT);
                } else {
                    c.setBackground(Style.C_SELECT);
                    c.setForeground(Style.C_TEXT);
                }
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                c.setFont(Style.FONT_NORMAL);
                return c;
            }
        });

        // Viewport dark aussi
        if (table.getParent() instanceof JViewport vp) {
            vp.setBackground(Style.BG_CARD);
        }
    }

    public static JPanel searchBar(JTextField field, JButton btnSearch, JButton btnReset) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        p.setBackground(Style.BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Style.C_BORDER, 1),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        JLabel ico = new JLabel("🔍");
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        p.add(ico);
        p.add(field);
        p.add(btnSearch);
        p.add(btnReset);
        return p;
    }

    public static void erreur(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    public static void succes(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
    public static boolean confirmer(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirmation",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }
}
