package kryptonbutterfly.totp.ui.prefs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import kryptonbutterfly.totp.TinyTotp;
import kryptonbutterfly.totp.TotpConstants;
import kryptonbutterfly.totp.misc.Password;
import kryptonbutterfly.totp.ui.misc.KeyTypedAdapter;
import kryptonbutterfly.totp.ui.prefs.cat.PasswordCat;
import kryptonbutterfly.totp.ui.prefs.cat.PrefsCat;
import kryptonbutterfly.totp.ui.prefs.cat.Time;
import kryptonbutterfly.totp.ui.prefs.cat.Updates;
import kryptonbutterfly.totp.ui.prefs.cat.WebcamCat;
import kryptonbutterfly.util.swing.ApplyAbortPanel;
import kryptonbutterfly.util.swing.ObservableDialog;
import kryptonbutterfly.util.swing.events.GuiCloseEvent;

@SuppressWarnings("serial")
public class PreferencesGui extends ObservableDialog<BL, Void, Void> implements TotpConstants
{
	private final KeyListener escapeListener = new KeyTypedAdapter(c -> this.dispose(), KeyEvent.VK_ESCAPE);
	
	final ArrayList<PrefsCat>	prefsCategories	= new ArrayList<>();
	private final JPanel		cards			= new JPanel();
	
	private final JPanel	bottomPanel	= new JPanel(new BorderLayout(0, 0));
	private final JTree		tree		= new JTree();
	
	private final HashSet<Runnable> treeSelectionListeners = new HashSet<>();
	
	public PreferencesGui(
		Window owner,
		ModalityType modality,
		Consumer<GuiCloseEvent<Void>> closeListener,
		Password password)
	{
		super(owner, modality, closeListener);
		setTitle("Preferences");
		TinyTotp.windowStates.prefsWindow.setBounds(this);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.25);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		final var cardLayout = new CardLayout(0, 0);
		cards.setLayout(cardLayout);
		splitPane.setRightComponent(cards);
		
		tree.addTreeSelectionListener(tsl -> treeSelectionListeners.forEach(Runnable::run));
		tree.addTreeSelectionListener(null);
		{
			tree.addTreeSelectionListener(e -> {
				if (e.getPath() == null)
					return;
				final var source = e.getPath().getLastPathComponent();
				if (source == null)
					return;
				if (source instanceof DefaultMutableTreeNode node)
					cardLayout.show(cards, (String) node.getUserObject());
				else
					System.err.printf("unexpected source of type %s\n", source.getClass().getSimpleName());
			});
			tree.setRootVisible(false);
			tree.setModel(
				new DefaultTreeModel(
					new DefaultMutableTreeNode("Root")
					{
						{
							add(addCategory(this, new Time(escapeListener)));
							add(addCategory(this, new Updates(escapeListener)));
							add(addCategory(this, new WebcamCat(PreferencesGui.this, escapeListener)));
							add(addCategory(this, new PasswordCat(escapeListener, password, treeSelectionListeners::add)));
						}
					}));
			scrollPane.setViewportView(tree);
			tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		}
		
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.add(new JSeparator(), BorderLayout.NORTH);
		
		businessLogic.if_(this::init);
		
		setVisible(true);
	}
	
	private void init(BL bl)
	{
		final var applyAbort = new ApplyAbortPanel(BUTTON_ABORT, bl::abort, BUTTON_APPLY, bl::apply);
		bottomPanel.add(applyAbort, BorderLayout.SOUTH);
		applyAbort.btnButton1.addKeyListener(escapeListener);
		applyAbort.btnButton2.addKeyListener(escapeListener);
		tree.addKeyListener(escapeListener);
	}
	
	@Override
	protected BL createBusinessLogic(Void args)
	{
		return new BL(this);
	}
	
	private DefaultMutableTreeNode addCategory(DefaultMutableTreeNode parent, PrefsCat item)
	{
		prefsCategories.add(item);
		cards.add(item.content(), item.prefsPath());
		final var node = new DefaultMutableTreeNode(item.prefsPath());
		item.init();
		parent.add(node);
		return node;
	}
}
