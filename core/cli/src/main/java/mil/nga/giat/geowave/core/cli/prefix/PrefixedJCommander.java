package mil.nga.giat.geowave.core.cli.prefix;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.IDefaultProvider;
import com.beust.jcommander.JCommander;

/**
 * This special JCommander instance does two things: 1. It initializes special
 * Prefixed argument objects (via addPrefixedObject) and adds them to the
 * JCommanders object list before parsing 2. It overrides the sub commands that
 * are added to make them instances of PrefixedJCommander 3. It lazily
 * initializes child commands using an Initializer interface.
 */
public class PrefixedJCommander extends
		JCommander
{
	private static Logger LOGGER = LoggerFactory.getLogger(PrefixedJCommander.class);

	// Allows us to override the commanders list that's being stored
	// in our parent class.
	private final Map<Object, JCommander> childCommanders;

	// A list of objects to add to the translator before feeding
	// into the internal JCommander object.
	private List<Object> prefixedObjects = null;

	private boolean validate = true;
	private boolean allowUnknown = false;
	private IDefaultProvider defaultProvider = null;

	// The map used to translate the variables back and forth.
	private JCommanderTranslationMap translationMap = null;

	// The initializer is used before parse to allow the user
	// to add additional commands/objects to this commander before
	// it is used
	private PrefixedJCommanderInitializer initializer = null;

	/**
	 * Creates a new instance of this commander
	 */
	@SuppressWarnings("unchecked")
	public PrefixedJCommander() {
		super();
		try {
			// HP Fortify "Access Specifier Manipulation"
			// This field is being modified by trusted code,
			// in a way that is not influenced by user input
			Field commandsField = JCommander.class.getDeclaredField("m_commands");
			commandsField.setAccessible(true);
			childCommanders = (Map<Object, JCommander>) commandsField.get(this);
		}
		catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			// This is a programmer error, and will only happen if another
			// version
			// of JCommander is being used.
			LOGGER.error(
					"Another version of JCommander is being used",
					e);
			throw new RuntimeException(
					e);
		}
	}

	/**
	 * This function will translate the given prefixed objects into the object
	 * list before parsing. This is so that their descriptions will be picked
	 * up.
	 */
	private void createMap() {
		if (translationMap != null) {
			throw new RuntimeException(
					"This PrefixedJCommander has already been used.");
		}

		// Initialize
		if (initializer != null) {
			initializer.initialize(this);
		}

		JCommanderPrefixTranslator translator = new JCommanderPrefixTranslator();

		// And these are the input to the translator!
		if (prefixedObjects != null) {
			for (Object obj : prefixedObjects) {
				translator.addObject(obj);
			}
		}

		translationMap = translator.translate();
		translationMap.createFacadeObjects();

		for (Object obj : translationMap.getObjects()) {
			addObject(obj);
		}

		// Copy default parameters over for parsing.
		translationMap.transformToFacade();
	}

	@Override
	public void addCommand(
			String name,
			Object object,
			String... aliases ) {
		super.addCommand(
				name,
				new Object(),
				aliases);

		// Super annoying. Can't control creation of JCommander objects, so
		// just replace it.

		Iterator<Entry<Object, JCommander>> iter = childCommanders.entrySet().iterator();
		Entry<Object, JCommander> last = null;
		while (iter.hasNext()) {
			last = iter.next();
		}

		PrefixedJCommander comm = new PrefixedJCommander();
		comm.setProgramName(
				name,
				aliases);
		comm.setDefaultProvider(defaultProvider);
		comm.setAcceptUnknownOptions(allowUnknown);
		comm.setValidate(validate);

		if (object != null) {
			comm.addPrefixedObject(object);
		}

		if (last != null) {
			childCommanders.put(
					last.getKey(),
					comm);
		}
	}

	/**
	 * Parse and validate the command line parameters. If validate flag is set,
	 * parse with validation, otherwise parse without
	 */
	@Override
	public void parse(
			String... args ) {
		createMap();
		if (validate) {
			super.parse(args);
		}
		else {
			super.parseWithoutValidation(args);
		}
		translationMap.transformToOriginal();
	}

	/**
	 * We replace the parseWithoutValidation() command with the setValidate
	 * option that we apply to all children. This is because of bug #267 in
	 * JCommander.
	 */
	@Override
	public void parseWithoutValidation(
			String... args ) {
		throw new NotImplementedException(
				"Do not use this method.  Use setValidate()");
	}

	@Override
	public void setDefaultProvider(
			IDefaultProvider defaultProvider ) {
		super.setDefaultProvider(defaultProvider);
		this.defaultProvider = defaultProvider;
	}

	/**
	 * Specify if unknown options should be accepted. If false, inputs will
	 * throw an error if unkown options are provided. If true, they will be
	 * accepted, though unknown options will be ignored.
	 */
	@Override
	public void setAcceptUnknownOptions(
			boolean allowUnknown ) {
		super.setAcceptUnknownOptions(allowUnknown);
		this.allowUnknown = allowUnknown;
	}

	/**
	 * Set the validate flag
	 * 
	 * @param validate
	 *            validate flag
	 */
	public void setValidate(
			boolean validate ) {
		this.validate = validate;
	}

	/**
	 * Get list of objects to add to the translator before feeding into the
	 * internal JCommander object.
	 * 
	 * @return list of objects to add to the translator before feeding into the
	 *         internal JCommander object.
	 */
	public List<Object> getPrefixedObjects() {
		return prefixedObjects;
	}

	/**
	 * Add new object to list of prefixed objects
	 * 
	 * @param object
	 *            object to add
	 */
	public void addPrefixedObject(
			Object object ) {
		if (this.prefixedObjects == null) {
			this.prefixedObjects = new ArrayList<>();
		}
		this.prefixedObjects.add(object);
	}

	/**
	 * Get the translation map
	 * 
	 * @return translation map
	 */
	public JCommanderTranslationMap getTranslationMap() {
		return translationMap;
	}

	/**
	 * Get the JCommander initializer
	 * 
	 * @return JCommander initializer
	 */
	public PrefixedJCommanderInitializer getInitializer() {
		return initializer;
	}

	/**
	 * Set the JCommander initializer
	 * 
	 * @param initializer
	 *            JCommander initializer to set
	 */
	public void setInitializer(
			PrefixedJCommanderInitializer initializer ) {
		this.initializer = initializer;
	}

	/**
	 * Interface for a prefixed JCommander initializer
	 */
	public interface PrefixedJCommanderInitializer
	{
		/**
		 * Initialize a commander
		 * 
		 * @param commander
		 *            commander to initialize
		 */
		void initialize(
				PrefixedJCommander commander );
	}
}
