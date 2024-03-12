package simulator.launcher;

import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;
import simulator.factories.*;
import simulator.misc.Utils;
import simulator.model.*;

public class Main {

	protected static Factory<Animal> _animals_factory;
	protected static Factory<Region> _regions_factory;
	private static int rows;
	private static int cols;
	private static int width;
	private static int height;

	private enum ExecMode {
		BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

		private String _tag;
		private String _desc;

		private ExecMode(String modeTag, String modeDesc) {
			_tag = modeTag;
			_desc = modeDesc;
		}

		public String get_tag() {
			return this._tag;
		}

		public String get_desc() {
			return this._desc;
		}
	}

	// default values for some parameters
	//
	private final static Double _default_time = 10.0; // in seconds
	private final static Double _default_delta_time = 0.03;

	// some attributes to stores values corresponding to command-line parameters
	//
	private static String _out_file = null;
	private static Double _time = null;
	private static Double _delta_time = null;
	private static String _in_file = null;
	private static boolean _simpleViewer = false;
	private static ExecMode _mode = ExecMode.BATCH;

	private static void parse_args(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = build_options();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parse_help_option(line, cmdLineOptions);
			parse_in_file_option(line);
			parse_time_option(line);
			parse_delta_time_option(line);
			parse_out_file_option(line);
			parse_simple_viewer_option(line);

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options build_options() {
		Options cmdLineOptions = new Options();

		// delta time
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
				.desc("A double representing actual time, in seconds, per simulation step. Default value: "
						+ _default_delta_time + ".")
				.build());

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions
				.addOption(Option.builder("i").longOpt("input").hasArg().desc("Initial configuration file.").build());

		// output file
		cmdLineOptions.addOption(
				Option.builder("o").longOpt("output").hasArg().desc("Output file, where output is written.").build());

		// simple viewer
		cmdLineOptions.addOption(
				Option.builder("sv").longOpt("simple-viewer").desc("Show the viewer window in console mode.").build());

		// steps
		cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
				.desc("An real number representing the total simulation time in seconds. Default value: "
						+ _default_time + ".")
				.build());

		return cmdLineOptions;
	}

	private static void parse_help_option(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parse_in_file_option(CommandLine line) throws ParseException {
		_in_file = line.getOptionValue("i");
		if (_mode == ExecMode.BATCH && _in_file == null) {
			throw new ParseException("In batch mode an input configuration file is required");
		}
	}

	private static void parse_out_file_option(CommandLine line) {
		_out_file = line.getOptionValue("o");
		if (_out_file == null)
			System.err.println("Output file not specified. Using default output location.");
	}

	private static void parse_time_option(CommandLine line) throws ParseException {
		String t = line.getOptionValue("t", _default_time.toString());
		try {
			_time = Double.parseDouble(t);
			assert (_time >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + t);
		}
	}

	private static void parse_simple_viewer_option(CommandLine line) {
		if (line.hasOption("sv"))
			_simpleViewer = true;
		else
			_simpleViewer = false;
	}

	private static void parse_delta_time_option(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _default_delta_time.toString());
		try {
			_delta_time = Double.parseDouble(dt);
			assert (_delta_time >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for delta-time: " + dt);
		}
	}

	private static void init_factories() {
		List<Builder<SelectionStrategy>> selection_strategy_builders = new ArrayList<>();
		selection_strategy_builders.add(new SelectFirstBuilder());
		selection_strategy_builders.add(new SelectClosestBuilder());
		selection_strategy_builders.add(new SelectYoungestBuilder());

		Factory<SelectionStrategy> selection_strategy_factory = new BuilderBasedFactory<>(selection_strategy_builders);

		_regions_factory = new BuilderBasedFactory<>(
				Arrays.asList(new DefaultRegionBuilder(), new DynamicSupplyRegionBuilder()));

		_animals_factory = new BuilderBasedFactory<>(Arrays.asList(new WolfBuilder(selection_strategy_factory),
				new SheepBuilder(selection_strategy_factory)));
	}

	private static JSONObject load_JSON_file(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}

	private static void start_batch_mode() throws Exception {
		InputStream is = new FileInputStream(new File(_in_file));
		JSONObject simulationData = load_JSON_file(is);
		width = simulationData.getInt("width");
		height = simulationData.getInt("height");
		cols = simulationData.getInt("cols");
		rows = simulationData.getInt("rows");
		is.close();

		String outputFileName = _out_file != null ? _out_file : "resources/tmp/myout.json";
		OutputStream os = new FileOutputStream(outputFileName);
		PrintStream print_stream = new PrintStream(os);

		Simulator _sim = new Simulator(cols, rows, width, height, _animals_factory, _regions_factory);
		Controller control = new Controller(_sim);
		control.load_data(simulationData);

		boolean simpleViewer = _simpleViewer;
		double time = _time != null ? _time : _default_time;
		double delta_time = _delta_time != null ? _delta_time : _default_delta_time;
		control.run(time, delta_time, simpleViewer, print_stream);

		List<? extends AnimalInfo> finalAnimals = _sim.get_animals();
		finalAnimals.forEach(animal -> {
			JSONObject animalJson = animal.as_JSON();
			print_stream.println(animalJson.toString(4));
		});

		print_stream.close();
	}

	private static void start_GUI_mode() throws Exception {
		throw new UnsupportedOperationException("GUI mode is not ready yet ...");
	}

	private static void start(String[] args) throws Exception {
		init_factories();
		parse_args(args);
		switch (_mode) {
		case BATCH:
			start_batch_mode();
			break;
		case GUI:
			start_GUI_mode();
			break;
		}
	}

	public static void main(String[] args) {
		Utils._rand.setSeed(2147483647l);
		try {
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}
