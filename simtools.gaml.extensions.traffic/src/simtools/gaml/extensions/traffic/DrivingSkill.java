/*******************************************************************************************************
 *
 * simtools.gaml.extensions.traffic.AdvancedDrivingSkill.java, in plugin simtools.gaml.extensions.traffic, is part of
 * the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package simtools.gaml.extensions.traffic;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.AbstractAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.graph.GraphTopology;
import msi.gama.metamodel.topology.graph.ISpatialGraph;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.graph.GamaGraph;
import msi.gama.util.path.GamaPath;
import msi.gama.util.path.IPath;
import msi.gama.util.path.PathFactory;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Random;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.operators.Spatial.Queries;
import msi.gaml.skills.MovingSkill;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

@vars ({ @variable (
		name = IKeyword.SPEED,
		type = IType.FLOAT,
		init = "1.0",
		doc = @doc ("the speed of the agent (in meter/second)")),
		@variable (
				name = IKeyword.REAL_SPEED,
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("the actual speed of the agent (in meter/second)")),
		@variable (
				name = "current_path",
				type = IType.PATH,
				init = "nil",
				doc = @doc ("the current path that tha agent follow")),
		@variable (
				name = "final_target",
				type = IType.POINT,
				init = "nil",
				doc = @doc ("the final target of the agent")),
		@variable (
				name = "current_target",
				type = IType.POINT,
				init = "nil",
				doc = @doc ("the current target of the agent")),
		@variable (
				name = "current_index",
				type = IType.INT,
				init = "0",
				doc = @doc ("the current index of the agent target (according to the targets list)")),
		@variable (
				name = "targets",
				type = IType.LIST,
				of = IType.POINT,
				init = "[]",
				doc = @doc ("the current list of points that the agent has to reach (path)")),
		@variable (
				name = "security_distance_coeff",
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc (
						deprecated = "use safety_distance_coeff instead",
						value = "the coefficient for the computation of the the min distance between two drivers (according to the vehicle speed - safety_distance =max(min_safety_distance, safety_distance_coeff `*` min(self.real_speed, other.real_speed) )")),
		@variable (
				name = "safety_distance_coeff",
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc ("the coefficient for the computation of the the min distance between two drivers (according to the vehicle speed - security_distance =max(min_security_distance, security_distance_coeff `*` min(self.real_speed, other.real_speed) )")),
		@variable (
				name = "min_security_distance",
				type = IType.FLOAT,
				init = "0.5",
				doc = @doc (
						deprecated = "use min_safety_distance instead",
						value = "the minimal distance to another driver")),
		@variable (
				name = "min_safety_distance",
				type = IType.FLOAT,
				init = "0.5",
				doc = @doc ("the minimal distance to another driver")),
		@variable (
				name = "current_lane",
				type = IType.INT,
				init = "0",
				doc = @doc ("the current lane on which the agent is")),
		@variable (
				name = "vehicle_length",
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("the length of the vehicle (in meters)")),
		@variable (
				name = "speed_coeff",
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc ("speed coefficient for the speed that the driver want to reach (according to the max speed of the road)")),
		@variable (
				name = "max_acceleration",
				type = IType.FLOAT,
				init = "0.5",
				doc = @doc ("maximum acceleration of the car for a cycle")),
		@variable (
				name = "current_road",
				type = IType.AGENT,
				doc = @doc ("current road on which the agent is")),
		@variable (
				name = "on_linked_road",
				type = IType.BOOL,
				init = "false",
				doc = @doc ("is the agent on the linked road?")),
		@variable (
				name = "proba_lane_change_up",
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc ("probability to change lane to a upper lane (left lane if right side driving) if necessary")),
		@variable (
				name = "proba_lane_change_down",
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc ("probability to change lane to a lower lane (right lane if right side driving) if necessary")),
		@variable (
				name = "proba_respect_priorities",
				type = IType.FLOAT,
				init = "1.0",
				doc = @doc ("probability to respect priority (right or left) laws")),
		@variable (
				name = "proba_respect_stops",
				type = IType.LIST,
				of = IType.FLOAT,
				init = "[]",
				doc = @doc ("probability to respect stop laws - one value for each type of stop")),
		@variable (
				name = "proba_block_node",
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("probability to block a node (do not let other driver cross the crossroad)")),
		@variable (
				name = "proba_use_linked_road",
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("probability to change lane to a linked road lane if necessary")),
		@variable (
				name = "right_side_driving",
				type = IType.BOOL,
				init = "true",
				doc = @doc ("are drivers driving on the right size of the road?")),
		@variable (
				name = "max_speed",
				type = IType.FLOAT,
				init = "50.0",
				doc = @doc ("maximal speed of the vehicle")),
		@variable (
				name = "distance_to_goal",
				type = IType.FLOAT,
				init = "0.0",
				doc = @doc ("euclidean distance to the next point of the current segment")),
		@variable (
				name = "segment_index_on_road",
				type = IType.INT,
				init = "-1",
				doc = @doc ("current segment index of the agent on the current road ")), })
@skill (
		name = "advanced_driving",
		concept = { IConcept.TRANSPORT, IConcept.SKILL },
		doc = @doc ("A skill that provides driving primitives and operators"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class DrivingSkill extends MovingSkill {

	static {
		DEBUG.OFF();
	}

	@Deprecated public final static String SECURITY_DISTANCE_COEFF = "security_distance_coeff";
	public final static String SAFETY_DISTANCE_COEFF = "safety_distance_coeff";
	@Deprecated public final static String MIN_SECURITY_DISTANCE = "min_security_distance";
	public final static String MIN_SAFETY_DISTANCE = "min_safety_distance";

	public final static String CURRENT_ROAD = "current_road";
	public final static String CURRENT_LANE = "current_lane";
	public final static String DISTANCE_TO_GOAL = "distance_to_goal";
	public final static String VEHICLE_LENGTH = "vehicle_length";
	public final static String PROBA_LANE_CHANGE_UP = "proba_lane_change_up";
	public final static String PROBA_LANE_CHANGE_DOWN = "proba_lane_change_down";
	public final static String PROBA_RESPECT_PRIORITIES = "proba_respect_priorities";
	public final static String PROBA_RESPECT_STOPS = "proba_respect_stops";
	public final static String PROBA_BLOCK_NODE = "proba_block_node";
	public final static String PROBA_USE_LINKED_ROAD = "proba_use_linked_road";
	public final static String RIGHT_SIDE_DRIVING = "right_side_driving";
	public final static String ON_LINKED_ROAD = "on_linked_road";
	public final static String TARGETS = "targets";
	public final static String CURRENT_TARGET = "current_target";
	public final static String CURRENT_INDEX = "current_index";
	public final static String FINAL_TARGET = "final_target";
	public final static String CURRENT_PATH = "current_path";
	public final static String ACCELERATION_MAX = "max_acceleration";
	public final static String SPEED_COEFF = "speed_coeff";
	public final static String MAX_SPEED = "max_speed";
	public final static String SEGMENT_INDEX = "segment_index_on_road";

	@getter (ACCELERATION_MAX)
	public double getAccelerationMax(final IAgent agent) {
		return (Double) agent.getAttribute(ACCELERATION_MAX);
	}

	@setter (ACCELERATION_MAX)
	public void setAccelerationMax(final IAgent agent, final Double val) {
		agent.setAttribute(ACCELERATION_MAX, val);
	}

	@getter (SPEED_COEFF)
	public double getSpeedCoeff(final IAgent agent) {
		return (Double) agent.getAttribute(SPEED_COEFF);
	}

	@setter (SPEED_COEFF)
	public void setSpeedCoeff(final IAgent agent, final Double val) {
		agent.setAttribute(SPEED_COEFF, val);
	}

	@getter (MAX_SPEED)
	public double getMaxSpeed(final IAgent agent) {
		return (Double) agent.getAttribute(MAX_SPEED);
	}

	@setter (MAX_SPEED)
	public void setMaxSpeed(final IAgent agent, final Double val) {
		agent.setAttribute(MAX_SPEED, val);
	}

	@getter (CURRENT_TARGET)
	public GamaPoint getCurrentTarget(final IAgent agent) {
		return (GamaPoint) agent.getAttribute(CURRENT_TARGET);
	}

	@setter (CURRENT_TARGET)
	public void setCurrentTarget(final IAgent agent, final ILocation point) {
		agent.setAttribute(CURRENT_TARGET, point);
	}

	@getter (FINAL_TARGET)
	public GamaPoint getFinalTarget(final IAgent agent) {
		return (GamaPoint) agent.getAttribute(FINAL_TARGET);
	}

	@setter (FINAL_TARGET)
	public void setFinalTarget(final IAgent agent, final ILocation point) {
		agent.setAttribute(FINAL_TARGET, point);
	}

	@getter (CURRENT_INDEX)
	public Integer getCurrentIndex(final IAgent agent) {
		return (Integer) agent.getAttribute(CURRENT_INDEX);
	}

	@setter (CURRENT_INDEX)
	public void setCurrentIndex(final IAgent agent, final Integer index) {
		agent.setAttribute(CURRENT_INDEX, index);
	}

	@getter (SEGMENT_INDEX)
	public Integer getSegmentIndex(final IAgent agent) {
		return (Integer) agent.getAttribute(SEGMENT_INDEX);
	}

	@setter (SEGMENT_INDEX)
	public void setSegmentIndex(final IAgent agent, final Integer index) {
		agent.setAttribute(SEGMENT_INDEX, index);
	}

	@Override
	@getter (CURRENT_PATH)
	public IPath getCurrentPath(final IAgent agent) {
		return (IPath) agent.getAttribute(CURRENT_PATH);
	}

	@Override
	@setter (CURRENT_PATH)
	public void setCurrentPath(final IAgent agent, final IPath path) {
		agent.setAttribute(CURRENT_PATH, path);
	}

	@getter (TARGETS)
	public List<ILocation> getTargets(final IAgent agent) {
		return (List<ILocation>) agent.getAttribute(TARGETS);
	}

	@setter (TARGETS)
	public void setTargets(final IAgent agent, final List<ILocation> points) {
		agent.setAttribute(TARGETS, points);
	}

	@getter (PROBA_USE_LINKED_ROAD)
	public double getProbaUseLinkedRoad(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_USE_LINKED_ROAD);
	}

	@setter (PROBA_USE_LINKED_ROAD)
	public void setProbaUseLinkedRoad(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_USE_LINKED_ROAD, proba);
	}

	@getter (PROBA_LANE_CHANGE_DOWN)
	public double getProbaLaneChangeDown(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_LANE_CHANGE_DOWN);
	}

	@setter (PROBA_LANE_CHANGE_DOWN)
	public void setProbaLaneChangeDown(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_LANE_CHANGE_DOWN, proba);
	}

	@getter (PROBA_LANE_CHANGE_UP)
	public double getProbaLaneChangeUp(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_LANE_CHANGE_UP);
	}

	@setter (PROBA_LANE_CHANGE_UP)
	public void setProbaLaneChangeUp(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_LANE_CHANGE_UP, proba);
	}

	@getter (PROBA_RESPECT_PRIORITIES)
	public double getRespectPriorities(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_RESPECT_PRIORITIES);
	}

	@setter (PROBA_RESPECT_PRIORITIES)
	public void setRespectPriorities(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_RESPECT_PRIORITIES, proba);
	}

	@getter (PROBA_BLOCK_NODE)
	public double getProbaBlockNode(final IAgent agent) {
		return (Double) agent.getAttribute(PROBA_BLOCK_NODE);
	}

	@setter (PROBA_BLOCK_NODE)
	public void setProbaBlockNode(final IAgent agent, final Double proba) {
		agent.setAttribute(PROBA_BLOCK_NODE, proba);
	}

	@getter (PROBA_RESPECT_STOPS)
	public List<Double> getRespectStops(final IAgent agent) {
		return (List<Double>) agent.getAttribute(PROBA_RESPECT_STOPS);
	}

	@setter (PROBA_RESPECT_STOPS)
	public void setRespectStops(final IAgent agent, final List<Boolean> probas) {
		agent.setAttribute(PROBA_RESPECT_STOPS, probas);
	}

	@getter (ON_LINKED_ROAD)
	public boolean getOnLinkedRoad(final IAgent agent) {
		return (Boolean) agent.getAttribute(ON_LINKED_ROAD);
	}

	@setter (ON_LINKED_ROAD)
	public void setOnLinkedRoad(final IAgent agent, final Boolean onLinkedRoad) {
		agent.setAttribute(ON_LINKED_ROAD, onLinkedRoad);
	}

	@getter (RIGHT_SIDE_DRIVING)
	public boolean getRightSideDriving(final IAgent agent) {
		return (Boolean) agent.getAttribute(RIGHT_SIDE_DRIVING);
	}

	@setter (RIGHT_SIDE_DRIVING)
	public void setRightSideDriving(final IAgent agent, final Boolean isRight) {
		agent.setAttribute(RIGHT_SIDE_DRIVING, isRight);
	}

	@Deprecated
	@getter (SECURITY_DISTANCE_COEFF)
	public double getSecurityDistanceCoeff(final IAgent agent) {
		return (Double) agent.getAttribute(SECURITY_DISTANCE_COEFF);
	}

	@Deprecated
	@setter (SECURITY_DISTANCE_COEFF)
	public void setSecurityDistanceCoeff(final IAgent agent, final double ls) {
		agent.setAttribute(SECURITY_DISTANCE_COEFF, ls);
	}

	@getter (SAFETY_DISTANCE_COEFF)
	public double getSafetyDistanceCoeff(final IAgent agent) {
		return (Double) agent.getAttribute(SAFETY_DISTANCE_COEFF);
	}

	@setter (SAFETY_DISTANCE_COEFF)
	public void setSafetyDistanceCoeff(final IAgent agent, final double ls) {
		agent.setAttribute(SAFETY_DISTANCE_COEFF, ls);
	}

	@getter (CURRENT_ROAD)
	public IAgent getCurrentRoad(final IAgent agent) {
		return (IAgent) agent.getAttribute(CURRENT_ROAD);
	}

	@getter (VEHICLE_LENGTH)
	public double getVehiculeLength(final IAgent agent) {
		return (Double) agent.getAttribute(VEHICLE_LENGTH);
	}

	@getter (CURRENT_LANE)
	public int getCurrentLane(final IAgent agent) {
		return (Integer) agent.getAttribute(CURRENT_LANE);
	}

	@getter (DISTANCE_TO_GOAL)
	public double getDistanceToGoal(final IAgent agent) {
		return (Double) agent.getAttribute(DISTANCE_TO_GOAL);
	}

	@getter (MIN_SECURITY_DISTANCE)
	public double getMinSecurityDistance(final IAgent agent) {
		return (Double) agent.getAttribute(MIN_SECURITY_DISTANCE);
	}

	@setter (MIN_SECURITY_DISTANCE)
	public void setMinSecDistance(final IAgent agent, final double msd) {
		agent.setAttribute(MIN_SECURITY_DISTANCE, msd);
	}

	@getter (MIN_SAFETY_DISTANCE)
	public double getMinSafetyDistance(final IAgent agent) {
		return (Double) agent.getAttribute(MIN_SAFETY_DISTANCE);
	}

	@setter (DISTANCE_TO_GOAL)
	public void setDistanceToGoal(final IAgent agent, final double dg) {
		agent.setAttribute(DISTANCE_TO_GOAL, dg);
	}

	public Double primAdvancedFollow(final IScope scope, final IAgent agent, final double s, final double t,
			final IPath path, final GamaPoint target) throws GamaRuntimeException {

		final double safety_distance_coeff = agent.hasAttribute(SAFETY_DISTANCE_COEFF) ? getSafetyDistanceCoeff(agent)
				: getSecurityDistanceCoeff(agent);
		final int currentLane = getCurrentLane(agent);
		final Double probaChangeLaneUp = getProbaLaneChangeUp(agent);
		final Double probaChangeLaneDown = getProbaLaneChangeDown(agent);
		final Double probaProbaUseLinkedRoad = getProbaUseLinkedRoad(agent);
		final Boolean rightSide = getRightSideDriving(agent);
		final IAgent currentRoad = getCurrentRoad(agent);
		final IAgent linkedRoad = (IAgent) currentRoad.getAttribute(RoadSkill.LINKED_ROAD);
		// final boolean onLinkedRoad = getOnLinkedRoad(agent);
		final double maxDist = computeDistance(scope, agent, s, t);

		if (maxDist == 0) { return 0.0; }
		// if (path != null && !path.getEdgeList().isEmpty()) {
		double tps = 0;
		// if ( onLinkedRoad ) {
		tps = t * moveToNextLocAlongPathOSM(scope, agent, path, target, maxDist, safety_distance_coeff, currentLane,
				currentRoad, linkedRoad, probaChangeLaneUp, probaChangeLaneDown, probaProbaUseLinkedRoad, rightSide);
		// }
		// else {
		// tps =
		// t *
		// moveToNextLocAlongPathOSM(scope, agent, path, target, maxDist,
		// security_distance, currentLane,
		// currentRoad, linkedRoad, probaChangeLaneUp, probaChangeLaneDown,
		// probaProbaUseLinkedRoad,
		// rightSide);
		// }

		if (tps < t) {
			agent.setAttribute(IKeyword.REAL_SPEED, this.getRealSpeed(agent) / (t - tps));
		} else {
			agent.setAttribute(IKeyword.REAL_SPEED, 0.0);
		}

		return tps;
		// }
		// return 0.0;
	}

	@action (
			name = "advanced_follow_driving",
			args = { @arg (
					name = "path",
					type = IType.PATH,
					optional = false,
					doc = @doc ("a path to be followed.")),
					@arg (
							name = "target",
							type = IType.POINT,
							optional = true,
							doc = @doc ("the target to reach")),
					@arg (
							name = IKeyword.SPEED,
							type = IType.FLOAT,
							optional = true,
							doc = @doc ("the speed to use for this move (replaces the current value of speed)")),
					@arg (
							name = "time",
							type = IType.FLOAT,
							optional = true,
							doc = @doc ("time to travel")) },
			doc = @doc (
					value = "moves the agent towards along the path passed in the arguments while considering the other agents in the network (only for graph topology)",
					returns = "the remaining time",
					examples = { @example ("do osm_follow path: the_path on: road_network;") }))
	public Double primAdvancedFollow(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final Double s = scope.hasArg(IKeyword.SPEED) ? scope.getFloatArg(IKeyword.SPEED) : getSpeed(agent);
		final Double t = scope.hasArg("time") ? scope.getFloatArg("time") : scope.getClock().getStepInSeconds();
		final GamaPoint target = scope.hasArg("target") ? (GamaPoint) scope.getArg("target", IType.NONE) : null;
		final GamaPath path = scope.hasArg("path") ? (GamaPath) scope.getArg("path", IType.NONE) : null;
		return primAdvancedFollow(scope, agent, s, t, path, target);
	}

	@action (
			name = "is_ready_next_road",
			args = { @arg (
					name = "new_road",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the road to test")),
					@arg (
							name = "lane",
							type = IType.INT,
							optional = false,
							doc = @doc ("the lane to test")) },
			doc = @doc (
					value = "action to test if the driver can take the given road at the given lane",
					returns = "true (the driver can take the road) or false (the driver cannot take the road)",
					examples = { @example ("do is_ready_next_road new_road: a_road lane: 0;") }))
	public Boolean primIsReadyNextRoad(final IScope scope) throws GamaRuntimeException {
		final IAgent road = (IAgent) scope.getArg("new_road", IType.AGENT);
		final Integer lane = (Integer) scope.getArg("lane", IType.INT);
		final IAgent driver = getCurrentAgent(scope);
		final double vL = getVehiculeLength(driver);
		final double secDistCoeff = driver.hasAttribute(SAFETY_DISTANCE_COEFF) ? getSafetyDistanceCoeff(driver)
				: getSecurityDistanceCoeff(driver);
		final double probaBlock = getProbaBlockNode(driver);
		final boolean testBlockNode = Random.opFlip(scope, probaBlock);
		final IAgent node = (IAgent) road.getAttribute(RoadSkill.SOURCE_NODE);
		final Map<IAgent, List<IAgent>> block = (Map<IAgent, List<IAgent>>) node.getAttribute(RoadNodeSkill.BLOCK);
		final List<IAgent> ba = GamaListFactory.create(scope, Types.AGENT, block.keySet());
		for (final IAgent dr : ba) {
			if (!dr.getLocation().equals(node.getLocation())) {
				block.remove(dr);
			}
		}
		return isReadyNextRoad(scope, road, driver, secDistCoeff, vL, block)
				&& (testBlockNode || nextRoadTestLane(driver, road, lane, secDistCoeff, vL));
	}

	@action (
			name = "test_next_road",
			args = { @arg (
					name = "new_road",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the road to test")) },
			doc = @doc (
					value = "action to test if the driver can take the given road",
					returns = "true (the driver can take the road) or false (the driver cannot take the road)",
					examples = { @example ("do test_next_road new_road: a_road;") }))
	public Boolean primTestNextRoad(final IScope scope) throws GamaRuntimeException {
		return true;
	}

	public Boolean isReadyNextRoad(final IScope scope, final IAgent road, final IAgent driver,
			final double secDistCoeff, final double vL, final Map<IAgent, List<IAgent>> block)
			throws GamaRuntimeException {
		final IAgent theNode = (IAgent) road.getAttribute(RoadSkill.SOURCE_NODE);
		final ISpecies context = driver.getSpecies();
		final IStatement.WithArgs actionTNR = context.getAction("test_next_road");
		final Arguments argsTNR = new Arguments();
		argsTNR.put("new_road", ConstantExpressionDescription.create(road));
		actionTNR.setRuntimeArgs(scope, argsTNR);
		// t41 += java.lang.System.currentTimeMillis() - tt2;
		// tt2 = java.lang.System.currentTimeMillis();
		if (!(Boolean) actionTNR.executeOn(scope)) { return false; }

		final List<List> stops = (List<List>) theNode.getAttribute(RoadNodeSkill.STOP);
		final List<Double> respectsStops = getRespectStops(driver);
		final IAgent currentRoad = (IAgent) driver.getAttribute(CURRENT_ROAD);
		if (currentRoad == null) { return true; }
		for (int i = 0; i < stops.size(); i++) {
			final Boolean stop = stops.get(i).contains(currentRoad);
			if (stop && (respectsStops.size() <= i || Random.opFlip(scope, respectsStops.get(i)))) { return false; }
		}
		for (final List<IAgent> rd : block.values()) {
			if (rd.contains(currentRoad)) { return false; }
		}
		final Boolean rightSide = getRightSideDriving(driver);
		final List<IAgent> priorityRoads = (List<IAgent>) theNode.getAttribute(RoadNodeSkill.PRIORITY_ROADS);
		final boolean onPriorityRoad = priorityRoads != null && priorityRoads.contains(currentRoad);
		final double angleRef = Punctal.angleInDegreesBetween(scope, (GamaPoint) theNode.getLocation(),
				(GamaPoint) currentRoad.getLocation(), (GamaPoint) road.getLocation());
		final List<IAgent> roadsIn = (List) theNode.getAttribute(RoadNodeSkill.ROADS_IN);
		if (!Random.opFlip(scope, getRespectPriorities(driver))) { return true; }
		final double realSpeed = Math.max(0.5, getRealSpeed(driver) + getAccelerationMax(driver));
		for (final IAgent rd : roadsIn) {
			if (rd != currentRoad) {
				final double angle = Punctal.angleInDegreesBetween(scope, (GamaPoint) theNode.getLocation(),
						(GamaPoint) currentRoad.getLocation(), (GamaPoint) rd.getLocation());
				final boolean isPriorityRoad = priorityRoads != null && priorityRoads.contains(rd);
				final boolean hasPriority = onPriorityRoad && !isPriorityRoad;
				final boolean shouldRespectPriority = !onPriorityRoad && isPriorityRoad;
				if (!hasPriority
						&& (shouldRespectPriority || rightSide && angle > angleRef || !rightSide && angle < angleRef)) {
					final int nbL = (Integer) rd.getAttribute(RoadSkill.LANES);
					final List<List<List<IAgent>>> agentsOn = (List) rd.getAttribute(RoadSkill.AGENTS_ON);
					for (int i = 0; i < nbL; i++) {
						final List<List<IAgent>> ags = agentsOn.get(i);
						final int nbS = ags.size();
						for (int j = 0; j < nbS; j++) {
							for (final IAgent pp : ags.get(j)) {
								final double vL2 = getVehiculeLength(pp);
								final double rp2 = getRealSpeed(pp);
								final double dist = pp.euclidianDistanceTo(driver);

								if (Maths.round(getRealSpeed(pp), 1) > 0.0 && 0.5
										+ secDistCoeff * Math.max(0, realSpeed - rp2) > dist - (vL2 / 2 + vL / 2)) {
									// rp2 * (7.0 / realSpeed + secDistCoeff) >
									// dist - vL2 / 2 - vL / 2 ) {
									// DEBUG.LOG("driver : " + driver +
									// " theNode : " + theNode +
									// " currentRoad : " + currentRoad + " pp :
									// " + pp + " dist : " + dist + " rp2 : " +
									// rp2 + " realSpeed : " + realSpeed + "
									// secDistCoeff : " + secDistCoeff);

									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	public boolean nextRoadTestLane(final IAgent driver, final IAgent road, final int lane, final double secDistCoeff,
			final double vL) {
		// double realSpeed = getRealSpeed(driver);
		// double secDist = 1 + realSpeed * secDistCoeff;
		List agentsOnTot = (List) road.getAttribute(RoadSkill.AGENTS_ON);
		if (agentsOnTot.isEmpty()) return false;
		final List agentsOn = (List) agentsOnTot.get(lane);
		for (final Object dd : agentsOn) {
			final List<IAgent> drivers = (List<IAgent>) dd;
			for (final IAgent dr : drivers) {
				if (dr == driver) {
					continue;
				}

				// if ( dr.euclidianDistanceTo(driver) < vL / 2 + secDist +
				// getVehiculeLength(dr) / 2 ) { return false; }
				if (GeometryUtils.getFirstPointOf(road).euclidianDistanceTo(dr) < vL / 2 + getVehiculeLength(dr) / 2) {
					return false;
				}
			}
		}
		return true;
	}

	@action (
			name = "compute_path",
			args = { @arg (
					name = "graph",
					type = IType.GRAPH,
					optional = false,
					doc = @doc ("the graph on wich compute the path")),
					@arg (
							name = "target",
							type = IType.AGENT,
							optional = false,
							doc = @doc ("the target node to reach")),
					@arg (
							name = "source",
							type = IType.AGENT,
							optional = true,
							doc = @doc ("the source node (optional, if not defined, closest node to the agent location)")),
					@arg (
							name = "on_road",
							type = IType.AGENT,
							optional = true,
							doc = @doc ("the road on which the agent is located (optional)")) },
			doc = @doc (
					value = "action to compute a path to a target location according to a given graph",
					returns = "the computed path, return nil if no path can be taken",
					examples = { @example ("do compute_path graph: road_network target: the_node;") }))
	public IPath primComputePath(final IScope scope) throws GamaRuntimeException {
		final ISpatialGraph graph = (ISpatialGraph) scope.getArg("graph", IType.GRAPH);
		final IAgent target = (IAgent) scope.getArg("target", IType.AGENT);
		final IAgent agent = getCurrentAgent(scope);
		RoadSkill.unregister(agent);
		IAgent source = (IAgent) scope.getArg("source", IType.AGENT);
		IAgent onRoad = (IAgent) scope.getArg("on_road", IType.AGENT);
		if (source == null) {
			if (onRoad != null) {
				source = RoadSkill.getTargetNode(onRoad);
			} else {
				source = (IAgent) Queries.closest_to(scope, target.getSpecies(), agent);
			}
		}
		if (source.getLocation().equals(agent.getLocation())) {
			onRoad = null;
		}

		final IPath path = ((GraphTopology) graph.getTopology(scope)).pathBetween(scope, source, target, onRoad);
		if (path != null && !path.getEdgeGeometry().isEmpty()) {
			final List<ILocation> targets = getTargets(agent);
			targets.clear();
			for (final Object edge : path.getEdgeGeometry()) {
				final IShape egGeom = (IShape) edge;
				final Coordinate[] coords = egGeom.getInnerGeometry().getCoordinates();
				final GamaPoint pt = new GamaPoint(coords[coords.length - 1]);
				targets.add(pt);
			}

			setTargets(agent, targets);
			setCurrentIndex(agent, 0);
			setCurrentTarget(agent, targets.get(0));
			setFinalTarget(agent, target.getLocation());
			setCurrentPath(agent, path);

			final IAgent nwRoad = (IAgent) path.getEdgeList().get(0);
			int lane = getCurrentLane(agent);
			if (lane > (Integer) nwRoad.getAttribute(RoadSkill.LANES)) {
				lane = (Integer) nwRoad.getAttribute(RoadSkill.LANES);
			}
			RoadSkill.register(nwRoad, agent, lane);

			return path;

		}
		setTargets(agent, GamaListFactory.<ILocation> create(Types.POINT));
		setCurrentTarget(agent, null);
		setFinalTarget(agent, null);
		setCurrentPath(agent, (IPath) null);
		return null;
	}

	@action (
			name = "path_from_nodes",
			args = { @arg (
					name = "graph",
					type = IType.GRAPH,
					optional = false,
					doc = @doc ("the graph on wich compute the path")),
					@arg (
							name = "nodes",
							type = IType.LIST,
							optional = false,
							doc = @doc ("the list of nodes composing the path")) },
			doc = @doc (
					value = "action to compute a path from a list of nodes according to a given graph",
					returns = "the computed path, return nil if no path can be taken",
					examples = { @example ("do compute_path graph: road_network nodes: [node1, node5, node10];") }))
	public IPath primComputePathFromNodes(final IScope scope) throws GamaRuntimeException {
		final GamaGraph graph = (GamaGraph) scope.getArg("graph", IType.GRAPH);
		final IList<IAgent> nodes = (IList) scope.getArg("nodes", IType.LIST);

		if (nodes == null || nodes.isEmpty()) { return null; }
		final IAgent source = nodes.firstValue(scope);
		final IAgent target = nodes.lastValue(scope);
		final IList edges = GamaListFactory.create();
		for (int i = 0; i < nodes.size() - 1; i++) {
			final Set<Object> eds = graph.getAllEdges(nodes.get(i), nodes.get(i + 1));
			if (!eds.isEmpty()) {
				double minW = Double.MAX_VALUE;
				Object ed = null;
				for (final Object e : eds) {
					final double w = graph.getEdgeWeight(e);
					if (w < minW) {
						minW = w;
						ed = e;
					}
				}
				edges.add(ed);
			} else {
				return null;
			}
		}
		if (edges.isEmpty()) { return null; }
		final IPath path = PathFactory.newInstance(graph, source, target, edges);
		final IAgent agent = getCurrentAgent(scope);
		if (path != null && !path.getEdgeGeometry().isEmpty()) {
			final List<ILocation> targets = getTargets(agent);
			targets.clear();
			for (final Object edge : path.getEdgeGeometry()) {
				final IShape egGeom = (IShape) edge;
				final Coordinate[] coords = egGeom.getInnerGeometry().getCoordinates();
				final GamaPoint pt = new GamaPoint(coords[coords.length - 1]);
				targets.add(pt);
			}
			setTargets(agent, targets);
			final IAgent nwRoad = (IAgent) path.getEdgeList().get(0);
			setCurrentIndex(agent, 0);
			setCurrentTarget(agent, targets.get(0));
			setFinalTarget(agent, target.getLocation());
			setCurrentPath(agent, path);
			RoadSkill.register(nwRoad, agent, 0);
			return path;

		}
		setTargets(agent, GamaListFactory.<ILocation> create(Types.POINT));
		setCurrentTarget(agent, null);
		setFinalTarget(agent, null);
		setCurrentPath(agent, (IPath) null);
		return null;
	}

	private Double speedChoice(final IAgent agent, final IAgent road) {
		return Math.min(getMaxSpeed(agent), Math.min(getRealSpeed(agent) + getAccelerationMax(agent),
				getSpeedCoeff(agent) * (Double) road.getAttribute(RoadSkill.MAXSPEED)));
	}

	/*
	 * static double t1 ; static double t2 ; static double t3 ; static double t4 ; static double t41 ; static double t42
	 * ; static double t43 ; static double t44 ; static double t45 ; static double t31 ; static double t32 ; static
	 * double t33 ; static double t34 ; static double t35 ; static double t36 ; static double t37 ;
	 *
	 * static double t341 ; static double t342 ; static double t343 ; static double t344 ; static double t345 ; static
	 * double t346 ; static double t347 ; static double tot; static int nbIt = 0;
	 */

	@action (
			name = "drive_random",
			args = { @arg (
					name = "proba_roads",
					type = IType.MAP,
					optional = true,
					doc = @doc ("a map containing for each road (key), the probability to be selected as next road (value)")) },
			doc = @doc (
					value = "action to drive by chosen randomly the next road",
					examples = { @example ("do drive_random;") }))
	public void primDriveRandom(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		final ISpecies context = agent.getSpecies();
		final IStatement.WithArgs actionImpactEF = context.getAction("external_factor_impact");
		final Arguments argsEF = new Arguments();
		final IStatement.WithArgs actionLC = context.getAction("lane_choice");
		final Arguments argsLC = new Arguments();
		final IStatement.WithArgs actionSC = context.getAction("speed_choice");
		final Arguments argsSC = new Arguments();
		final Map<IAgent, Double> roadProba = (Map) scope.getArg("proba_roads", IType.MAP);

		double remainingTime = scope.getSimulation().getClock().getStepInSeconds();
		while (remainingTime > 0.0) {
			final IAgent road = getCurrentRoad(agent);
			final GamaPoint target = GeometryUtils.getLastPointOf(road);
			argsSC.put("new_road", ConstantExpressionDescription.create(road));
			actionSC.setRuntimeArgs(scope, argsSC);
			final double speed = (Double) actionSC.executeOn(scope);
			setSpeed(agent, speed);
			remainingTime = primAdvancedFollow(scope, agent, speed, remainingTime, null, target);

			if (remainingTime > 0.0) {
				IAgent newRoad = null;
				final IAgent targetNode = (IAgent) road.getDirectVarValue(scope, RoadSkill.TARGET_NODE);
				final List<IAgent> nextRoads = (List) targetNode.getDirectVarValue(scope, RoadNodeSkill.ROADS_OUT);
				if (nextRoads.isEmpty()) { return; }
				if (nextRoads.size() == 1) {
					newRoad = nextRoads.get(0);
				}
				if (nextRoads.size() > 1) {
					if (roadProba == null || roadProba.isEmpty()) {
						newRoad = nextRoads.get(scope.getRandom().between(0, nextRoads.size() - 1));
					} else {
						final IList<Double> distribution = GamaListFactory.create(Types.FLOAT);
						for (final IAgent r : nextRoads) {
							final Double val = roadProba.get(r);
							distribution.add(val == null ? 0.0 : val);
						}
						newRoad = nextRoads.get(Random.opRndChoice(scope, distribution));
					}
				}

				argsEF.put("remaining_time", ConstantExpressionDescription.create(remainingTime));
				argsEF.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionImpactEF.setRuntimeArgs(scope, argsEF);
				remainingTime = (Double) actionImpactEF.executeOn(scope);
				argsLC.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionLC.setRuntimeArgs(scope, argsLC);
				final int lane = (Integer) actionLC.executeOn(scope);

				if (lane >= 0) {
					RoadSkill.register(newRoad, agent, lane);
				} else {
					return;
				}
			}
		}
	}

	@action (
			name = "drive",
			doc = @doc (
					value = "action to drive toward the final target",
					examples = { @example ("do drive;") }))
	public void primDrive(final IScope scope) throws GamaRuntimeException {

		/*
		 * nbIt++; if (nbIt % 10000 == 0) { DEBUG.OUT("tot: " + tot + " t1: " + t1 + " t2: " + t2 + " t3: " + t3 +
		 * " t31: " + t31 + " t32: " + t32 + " t33: " + t33 + " t34: " + t34 + " t35: " + t35 + " t36: " + t36 +
		 * " t37: " + t37 + " t341: " + t341 + " t342: " + t342 + " t343: " + t343 + " t344: " + t344 + " t345: " + t345
		 * + " t346: " + t346 + " t347: " + t347 + " t4:" + t4 + " t41:" + t41+ " t42:" + t42+ " t43:" + t43+ " t44:" +
		 * t44+ " t45:" + t45);
		 *
		 * } long tt22 = java.lang.System.currentTimeMillis();
		 *
		 * long t = java.lang.System.currentTimeMillis();
		 */

		final IAgent agent = getCurrentAgent(scope);
		if (agent == null || agent.dead()) { return; }
		final GamaPoint finalTarget = getFinalTarget(agent);
		if (finalTarget == null) { return; }
		final IPath path = getCurrentPath(agent);

		final double fx = finalTarget.getX();
		final double fy = finalTarget.getY();

		final ISpecies context = agent.getSpecies();
		final IStatement.WithArgs actionImpactEF = context.getAction("external_factor_impact");
		final Arguments argsEF = new Arguments();
		final IStatement.WithArgs actionLC = context.getAction("lane_choice");
		final Arguments argsLC = new Arguments();
		final IStatement.WithArgs actionSC = context.getAction("speed_choice");
		final Arguments argsSC = new Arguments();
		ILocation loc = agent.getLocation();
		double x = loc.getX();
		double y = loc.getY();

		double remainingTime = scope.getSimulation().getClock().getStepInSeconds();
		// t1 += java.lang.System.currentTimeMillis() - t;
		while (remainingTime > 0.0) {
			// t = java.lang.System.currentTimeMillis();

			final IAgent road = getCurrentRoad(agent);
			final GamaPoint target = getCurrentTarget(agent);
			final double xt = target.getX();
			final double yt = target.getY();
			argsSC.put("new_road", ConstantExpressionDescription.create(road));
			actionSC.setRuntimeArgs(scope, argsSC);
			final double speed = (Double) actionSC.executeOn(scope);
			setSpeed(agent, speed);
			// t2 += java.lang.System.currentTimeMillis() - t;
			// t = java.lang.System.currentTimeMillis();
			remainingTime = primAdvancedFollow(scope, agent, speed, remainingTime, path, target);
			loc = agent.getLocation();
			x = loc.getX();
			y = loc.getY();
			// t3 += java.lang.System.currentTimeMillis() - t;
			// t = java.lang.System.currentTimeMillis();
			if (x == fx && y == fy) {
				setFinalTarget(agent, null);

				// tot += java.lang.System.currentTimeMillis() - tt22;
				return;
			}

			if (remainingTime > 0.0 && x == xt && y == yt) {
				Integer currentIndex = getCurrentIndex(agent);
				if (currentIndex >= path.getEdgeList().size() - 1) {
					setCurrentPath(agent, (IPath) null);
					setFinalTarget(agent, null);
					// t4 += java.lang.System.currentTimeMillis() - t;

					// tot += java.lang.System.currentTimeMillis() - tt22;
					return;
				}
				// long tt2 = java.lang.System.currentTimeMillis();
				final IAgent newRoad = (IAgent) path.getEdgeList().get(currentIndex + 1);
				argsEF.put("remaining_time", ConstantExpressionDescription.create(remainingTime));
				argsEF.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionImpactEF.setRuntimeArgs(scope, argsEF);
				// t41 += java.lang.System.currentTimeMillis() - tt2;
				// tt2 = java.lang.System.currentTimeMillis();
				remainingTime = (Double) actionImpactEF.executeOn(scope);
				// t42 += java.lang.System.currentTimeMillis() - tt2;
				// tt2 = java.lang.System.currentTimeMillis();
				argsLC.put("new_road", ConstantExpressionDescription.create(newRoad));
				actionLC.setRuntimeArgs(scope, argsLC);
				final int lane = (Integer) actionLC.executeOn(scope);
				// t43 += java.lang.System.currentTimeMillis() - tt2;
				// tt2 = java.lang.System.currentTimeMillis();

				if (lane >= 0 ) {
					currentIndex = currentIndex + 1;
					setCurrentIndex(agent, currentIndex);
					RoadSkill.register(newRoad, agent, lane);
					setCurrentTarget(agent, getTargets(agent).get(currentIndex));
				} else {
					// t4 += java.lang.System.currentTimeMillis() - t;

					// tot += java.lang.System.currentTimeMillis() - tt22;
					// t44 += java.lang.System.currentTimeMillis() - tt2;
					return;
				}
			}
			// t4 += java.lang.System.currentTimeMillis() - t;

		}
		// tot += java.lang.System.currentTimeMillis() - tt22;

	}

	@action (
			name = "external_factor_impact",
			args = { @arg (
					name = "new_road",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the road on which to the driver wants to go")),
					@arg (
							name = "remaining_time",
							type = IType.FLOAT,
							optional = false,
							doc = @doc ("the remaining time")) },
			doc = @doc (
					value = "action that allows to define how the remaining time is impacted by external factor",
					returns = "the remaining time",
					examples = { @example ("do external_factor_impact new_road: a_road remaining_time: 0.5;") }))
	public Double primExternalFactorOnRemainingTime(final IScope scope) throws GamaRuntimeException {
		return scope.getFloatArg("remaining_time");
	}

	@action (
			name = "speed_choice",
			args = { @arg (
					name = "new_road",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the road on which to choose the speed")) },
			doc = @doc (
					value = "action to choose a speed",
					returns = "the chosen speed",
					examples = { @example ("do speed_choice new_road: the_road;") }))
	public Double primSpeedChoice(final IScope scope) throws GamaRuntimeException {
		final IAgent road = (IAgent) scope.getArg("new_road", IType.AGENT);
		final IAgent agent = getCurrentAgent(scope);
		final double speed = speedChoice(agent, road);
		setSpeed(agent, speed);
		return speed;
	}

	public Integer laneChoice(final IScope scope, final IAgent road) throws GamaRuntimeException {

		final Integer lanes = (Integer) road.getAttribute(RoadSkill.LANES);
		final IAgent driver = getCurrentAgent(scope);
		Integer currentLane = getCurrentLane(driver);
		final IAgent currentRoad = getCurrentRoad(driver);
		// final IAgent linkedRoad = (IAgent) road.getAttribute(RoadSkill.LINKED_ROAD);
		final boolean onLinkedRoad = getOnLinkedRoad(driver);
		final IAgent node = (IAgent) road.getAttribute(RoadSkill.SOURCE_NODE);
		final double vL = getVehiculeLength(driver);
		final double secDistCoeff = driver.hasAttribute(SAFETY_DISTANCE_COEFF) ? getSafetyDistanceCoeff(driver)
				: getSecurityDistanceCoeff(driver);
		final double probaBlock = getProbaBlockNode(driver);
		final boolean testBlockNode = Random.opFlip(scope, probaBlock);
		final Map<IAgent, List<IAgent>> block = (Map<IAgent, List<IAgent>>) node.getAttribute(RoadNodeSkill.BLOCK);
		final Collection<IAgent> ba = new HashSet<>(block.keySet());
		final List<IAgent> roadsIn = (List<IAgent>) node.getAttribute(RoadNodeSkill.ROADS_IN);

		// final double probaUseLinkedRoad = getProbaUseLinkedRoad(driver);
		// final boolean testUseLinkedRoad = false;// Random.opFlip(scope,
		// probaUseLinkedRoad);

		if (onLinkedRoad) {
			currentLane = lanes - 1;
		}
		for (final IAgent dr : ba) {
			if (!dr.getLocation().equals(node.getLocation())) {
				block.remove(dr);
			}
		}

		final boolean ready = isReadyNextRoad(scope, road, driver, secDistCoeff, vL, block);
		if (!ready) { return -1; }

		if (lanes == 0 /* && !onLinkedRoad */ ) {
			final int lane = testBlockNode || nextRoadTestLane(driver, road, 0, secDistCoeff, vL) ? 0 : -1;

			if (lane != -1) {
				addBlockingDriver(0, testBlockNode, driver, currentRoad, road, node, roadsIn, block);
				return lane;
			}
			// else if (linkedRoad != null && testUseLinkedRoad) {
			// final Integer lanesLinked = (Integer)
			// linkedRoad.getAttribute(RoadSkill.LANES);
			// final int newLane = nextRoadTestLane(driver, linkedRoad,
			// lanesLinked - 1, secDistCoeff, vL)
			// ? lanesLinked - 1 : -1;
			// if (newLane > -1) {
			// return newLane + lanes;
			// }
			// }
			return lane;
		}
		final int cvTmp = Math.min(currentLane, lanes - 1);
		int cv = testBlockNode || nextRoadTestLane(driver, road, cvTmp, secDistCoeff, vL) ? cvTmp : -1;

		if (cv != -1) {
			addBlockingDriver(cv, testBlockNode, driver, currentRoad, road, node, roadsIn, block);
			return cv;
		}
		final double probaLaneChangeDown = getProbaLaneChangeDown(driver);
		final double probaLaneChangeUp = getProbaLaneChangeUp(driver);
		final boolean changeDown = Random.opFlip(scope, probaLaneChangeDown);
		final boolean changeUp = Random.opFlip(scope, probaLaneChangeUp);
		if (changeDown || changeUp) {
			for (int i = 0; i < lanes; i++) {
				final int l1 = cvTmp - i;
				if (l1 >= 0 && changeDown) {
					cv = testBlockNode || nextRoadTestLane(driver, road, l1, secDistCoeff, vL) ? l1 : -1;
					if (cv != -1) {
						addBlockingDriver(cv, testBlockNode, driver, currentRoad, road, node, roadsIn, block);
						return cv;
					}
				}
				final int l2 = cvTmp + i;
				if (l2 < lanes && changeUp) {
					cv = testBlockNode || nextRoadTestLane(driver, road, l2, secDistCoeff, vL) ? l2 : -1;
					if (cv != -1) {
						addBlockingDriver(cv, testBlockNode, driver, currentRoad, road, node, roadsIn, block);
						return cv;
					}
				}
			}
		}

		// if (cv == -1 && linkedRoad != null && testUseLinkedRoad) {
		// final Integer lanesLinked = (Integer)
		// linkedRoad.getAttribute(RoadSkill.LANES);
		// for (int i = 1; i <= lanesLinked; i++) {
		// final int newLane = nextRoadTestLane(driver, linkedRoad, lanesLinked
		// - i, secDistCoeff, vL)
		// ? lanesLinked - i : -1;
		// if (newLane > -1) {
		// return newLane + lanes;
		// }
		// }
		// }

		return cv;
	}

	@action (
			name = "lane_choice",
			args = { @arg (
					name = "new_road",
					type = IType.AGENT,
					optional = false,
					doc = @doc ("the road on which to choose the lane")) },
			doc = @doc (
					value = "action to choose a lane",
					returns = "the chosen lane, return -1 if no lane can be taken",
					examples = { @example ("do lane_choice new_road: a_road;") }))
	public Integer primLaneChoice(final IScope scope) throws GamaRuntimeException {
		final IAgent road = (IAgent) scope.getArg("new_road", IType.AGENT);
		return laneChoice(scope, road);
	}

	public void addBlockingDriver(final int cv, final boolean testBlockNode, final IAgent driver,
			final IAgent currentRoad, final IAgent road, final IAgent node, final List<IAgent> roadsIn,
			final Map<IAgent, List<IAgent>> block) {
		if (testBlockNode && roadsIn.size() > 1) {
			final List<IAgent> rb = roadBlocked(currentRoad, road, node, roadsIn);
			if (!rb.isEmpty()) {
				block.put(driver, rb);
			}
		}
	}

	public List<IAgent> roadBlocked(final IAgent roadIn, final IAgent roadOut, final IAgent node,
			final List<IAgent> roadsIn) {
		final List<IAgent> roadsBlocked = GamaListFactory.create(Types.AGENT);
		for (final IAgent rd : roadsIn) {
			if ( /* rd != roadIn && */ !rd.getLocation().equals(roadOut.getLocation())) {
				roadsBlocked.add(rd);
			}
		}
		return roadsBlocked;
	}

	/**
	 * @throws GamaRuntimeException
	 *             Return the next location toward a target on a line
	 *
	 * @param coords
	 *            coordinates of the line
	 * @param source
	 *            current location
	 * @param target
	 *            location to reach
	 * @param distance
	 *            max displacement distance
	 * @return the next location
	 */

	protected double computeDistance(final IScope scope, final IAgent agent, final double s, final double t)
			throws GamaRuntimeException {

		return s * t * scope.getClock().getStepInSeconds();
	}

	public double distance2D(final GamaPoint p1, final GamaPoint p2) {
		return p1.distance(p2);
	}

	private double avoidCollision(final IScope scope, final IAgent agent, final double distance,
			final double security_distance, final GamaPoint currentLocation, final GamaPoint target, final int lane,
			final int segmentIndex, final boolean onLinkedRoad, final IAgent currentRoad, final boolean changeLane) {
		// long t = java.lang.System.currentTimeMillis();
		// DEBUG.OUT(agent + " agents: " + agents);
		final double distanceToGoal = getDistanceToGoal(agent);
		final boolean nextSegment = distanceToGoal < distance;
		final double min_safety_distance =
				agent.hasAttribute(MIN_SAFETY_DISTANCE) ? getMinSafetyDistance(agent) : getMinSecurityDistance(agent);
		// final IAgent theRoad = onLinkedRoad ?
		// RoadSkill.getLinkedRoad(currentRoad) : currentRoad;
		final int segment =
				onLinkedRoad ? currentRoad.getInnerGeometry().getNumPoints() - 2 - segmentIndex : segmentIndex;
		final IList aglanes = (IList) ((IList) currentRoad.getAttribute(RoadSkill.AGENTS_ON)).get(lane);
		final Collection<IAgent> agents = (IList<IAgent>) aglanes.get(segment);
		final boolean moreSegment = !onLinkedRoad && segmentIndex <= aglanes.size() - 2;

		final boolean contains = agents.contains(agent);
		final GamaPoint targetLoc = new GamaPoint(currentRoad.getInnerGeometry().getCoordinates()[segment + 1]);
		// DEBUG.OUT("agent:" + agent + " theRoad: " +
		// theRoad + " segment: " + segment + " targetLoc: " + targetLoc);
		// if (changeLane && onLinkedRoad)
		// DEBUG.OUT("agent:" + agent + " contains: " +
		// contains + " distanceToGoal:" + distanceToGoal);
		final double vL = getVehiculeLength(agent);
		if (contains && agents.size() < 2 || !contains && agents.isEmpty()) {
			// if (changeLane && onLinkedRoad)
			// DEBUG.OUT("<2 agent:" + agent + " nextSegment:
			// " + nextSegment + " moreSegment: " + moreSegment);

			if (changeLane && distance < vL) { return 0; }
			if (onLinkedRoad && nextSegment) { return 0; }
			if (nextSegment && moreSegment) {
				final Collection<IAgent> ags = (Collection<IAgent>) aglanes.get(segment + 1);
				final double length = currentRoad.getInnerGeometry().getCoordinates()[segment + 2].distance(targetLoc);
				for (final IAgent ag : ags) {
					final double distTG = getOnLinkedRoad(ag) ? distance2D((GamaPoint) ag.getLocation(), targetLoc)
							: getDistanceToGoal(ag);
					final double vLa = 0.5 * vL + 0.5 * getVehiculeLength(ag);
					if (length - distTG < vLa) { return distanceToGoal - (vLa - (length - distTG)); }
				}
				return distance;
			}
			return distance;
		}
		// t341+= java.lang.System.currentTimeMillis() - t;
		// t = java.lang.System.currentTimeMillis();

		// distance2D((GamaPoint) agent.getLocation(),
		// target);//agent.euclidianDistanceTo(target);//
		// getDistanceToGoal(agent);
		IAgent nextAgent = null;
		double minDiff = Double.MAX_VALUE;
		// t343+= java.lang.System.currentTimeMillis() - t;
		// t = java.lang.System.currentTimeMillis();

		if (onLinkedRoad) {
			if (onLinkedRoad && nextSegment) { return 0; }

			for (final IAgent ag : agents) {
				// if (changeLane && onLinkedRoad)
				// DEBUG.OUT("ag: " + ag );

				if (ag == agent || ag == null) {
					continue;
				}
				final double dist = distance2D((GamaPoint) ag.getLocation(), targetLoc);

				final double diff = distanceToGoal - dist;
				// if (changeLane && onLinkedRoad)
				// DEBUG.OUT("dist:" + dist + " diff: " +
				// diff);

				if (changeLane && Math.abs(diff) < vL) { return 0; }
				if (diff <= 0.0) {
					continue;
				}
				if (diff < minDiff) {
					minDiff = diff;
					nextAgent = ag;
				}
			}
		} else {

			for (final IAgent ag : agents) {
				// DEBUG.OUT("ag: " + ag + " currentRoad:" +
				// currentRoad);
				if (ag == agent || ag == null) {
					continue;
				}

				final double dist = getOnLinkedRoad(ag) ? distance2D((GamaPoint) ag.getLocation(), targetLoc)
						: getDistanceToGoal(ag);// distance2D((GamaPoint)
												// ag.getLocation(), target);
				final double diff = distanceToGoal - dist;
				// DEBUG.OUT("ag: " + ag + " dist: " + dist);
				if (changeLane && Math.abs(diff) < vL) { return 0; }
				if (diff <= 0.0) {
					continue;
				}
				if (diff < minDiff) {
					minDiff = diff;
					nextAgent = ag;
				}
			}
		}

		// if (changeLane && onLinkedRoad)
		// DEBUG.OUT("agent : " + agent + " minDiff : " +
		// minDiff + " nextAgent : " + nextAgent);
		// t344+= java.lang.System.currentTimeMillis() - t;
		// t = java.lang.System.currentTimeMillis();
		// DEBUG.OUT("nextAgent" + nextAgent + " minDiff: " +
		// minDiff);

		// DEBUG.OUT("agent:" + agent + " nextAgent: " +
		// nextAgent);

		if (nextAgent == null) {
			// DEBUG.OUT("agent:" + agent + " nextSegment: "
			// + nextSegment+ " moreSegment: " + moreSegment);

			if (nextSegment && moreSegment) {
				final Collection<IAgent> ags = (Collection<IAgent>) aglanes.get(segment + 1);
				final double length = currentRoad.getInnerGeometry().getCoordinates()[segment + 1]
						.distance(currentRoad.getInnerGeometry().getCoordinates()[segment + 2]);
				// DEBUG.OUT("length: " + length);

				for (final IAgent ag : ags) {
					final double distTG = getOnLinkedRoad(ag) ? distance2D((GamaPoint) ag.getLocation(), targetLoc)
							: getDistanceToGoal(ag);
					final double vLa = 0.5 * vL + 0.5 * getVehiculeLength(ag);
					// DEBUG.OUT("distTG: " + distTG + " vLa:
					// " + vLa );
					if (distTG > length - vLa) { return distanceToGoal - (vLa - (length - distTG)); }
				}
				return distance;
			}
			return distance;
		}
		double secDistance = 0.0;
		if (getOnLinkedRoad(nextAgent) == getOnLinkedRoad(agent)) {
			secDistance = Math.max(min_safety_distance,
					security_distance * Math.min(getRealSpeed(agent), getRealSpeed(nextAgent)));
		} else {
			secDistance = Math.max(min_safety_distance,
					security_distance * Math.max(getRealSpeed(agent), getRealSpeed(nextAgent)));
		}
		double realDist = Math.min(distance, minDiff - secDistance - 0.5 * vL - 0.5 * getVehiculeLength(nextAgent));
		// t345+= java.lang.System.currentTimeMillis() - t;

		if (changeLane && realDist < vL) { return 0; }
		realDist = Math.max(0.0, (int) (min_safety_distance + realDist * 1000) / 1000.0);
		// DEBUG.OUT("realDist" + realDist + " secDistance: "
		// + secDistance);

		return realDist;
	}

	private void changeLanetoReverse(final IScope scope, final IAgent agent, final int previousLane, final int newLane,
			final int segment, final List currentAgentOn, final List newAgentOn) {
		final int oldIndex = getSegmentIndex(agent);
		final List agPrevLane = (List) currentAgentOn.get(previousLane);
		final int newIndexInv = agPrevLane.size() - segment - 1;
		agent.setAttribute(CURRENT_LANE, newLane);
		((List) agPrevLane.get(oldIndex)).remove(agent);
		final List ags = (List) newAgentOn.get(newLane);
		((List) ags.get(newIndexInv)).add(agent);
		agent.setAttribute(SEGMENT_INDEX, segment);
	}

	private void changeLanefromReverse(final IScope scope, final IAgent agent, final int previousLane,
			final int newLane, final int segment, final List currentAgentOn, final List newAgentOn) {
		final int oldIndex = getSegmentIndex(agent);
		final List agPrevLane = (List) currentAgentOn.get(previousLane);
		final int oldIndexInv = agPrevLane.size() - oldIndex - 1;
		agent.setAttribute(CURRENT_LANE, newLane);
		((List) agPrevLane.get(oldIndexInv)).remove(agent);
		final List ags = (List) newAgentOn.get(newLane);
		((List) ags.get(segment)).add(agent);
		agent.setAttribute(SEGMENT_INDEX, segment);
	}

	private void changeLaneReverse(final IScope scope, final IAgent agent, final int previousLane, final int newLane,
			final int segment, final List currentAgentOn, final List newAgentOn) {
		final int oldIndex = getSegmentIndex(agent);
		final List agPrevLane = (List) currentAgentOn.get(previousLane);
		final int oldIndexInv = agPrevLane.size() - oldIndex - 1;
		final int newIndexInv = agPrevLane.size() - segment - 1;
		agent.setAttribute(CURRENT_LANE, newLane);
		((List) agPrevLane.get(oldIndexInv)).remove(agent);
		final List ags = (List) newAgentOn.get(newLane);
		((List) ags.get(newIndexInv)).add(agent);
		agent.setAttribute(SEGMENT_INDEX, segment);
	}

	private void changeLane(final IScope scope, final IAgent agent, final int previousLane, final int newLane,
			final int segment, final List currentAgentOn, final List newAgentOn) {
		agent.setAttribute(CURRENT_LANE, newLane);
		((List) ((List) currentAgentOn.get(previousLane)).get(getSegmentIndex(agent))).remove(agent);
		final List ags = (List) newAgentOn.get(newLane);
		((List) ags.get(segment)).add(agent);
		agent.setAttribute(SEGMENT_INDEX, segment);
	}

	private double avoidCollisionLinkedRoad(final IScope scope, final IAgent agent, final double distance,
			final double security_distance, final GamaPoint currentLocation, final GamaPoint target, final int lane,
			final int segment, final IAgent currentRoad, final IAgent linkedRoad, final Double probaChangeLaneUp,
			final Double probaChangeLaneDown, final Double probaUseLinkedRoad, final Boolean rightSide) {
		double distMax = 0;
		int newLane = lane;
		final int nbLinkedLanes = (Integer) linkedRoad.getAttribute(RoadSkill.LANES);
		final int nbLanes = (Integer) currentRoad.getAttribute(RoadSkill.LANES);
		final List agentOnCurrentRoad = (List) currentRoad.getAttribute(RoadSkill.AGENTS_ON);
		final List agentOnLinkedRoad = (List) linkedRoad.getAttribute(RoadSkill.AGENTS_ON);
		List newAgentOn = agentOnLinkedRoad;
		boolean onLinkedRoad = true;

		if (scope.getRandom().next() < probaChangeLaneDown) {
			if (lane < nbLinkedLanes - 1) {

				final double val = avoidCollision(scope, agent, distance, security_distance, currentLocation, target,
						lane + 1, segment, true, currentRoad, true);
				if (val == distance) {
					newLane = lane + 1;
					changeLaneReverse(scope, agent, lane, lane, segment, agentOnLinkedRoad, agentOnLinkedRoad);
					return distance;
				}
				if (val > distMax && val > 0) {
					newLane = lane + 1;
					distMax = val;
				}
			} else {
				final double val = avoidCollision(scope, agent, distance, security_distance, currentLocation, target,
						nbLanes - 1, segment, false, currentRoad, true);
				if (val == distance) {
					newLane = nbLanes - 1;
					setOnLinkedRoad(agent, false);
					changeLanefromReverse(scope, agent, lane, newLane, segment, agentOnLinkedRoad, agentOnCurrentRoad);
					return distance;
				}
				if (val > distMax && val > 0) {
					newLane = nbLanes - 1;
					newAgentOn = agentOnCurrentRoad;
					distMax = val;
					onLinkedRoad = false;
				}
			}
		}
		double val = avoidCollision(scope, agent, distance, security_distance, currentLocation, target, lane, segment,
				true, currentRoad, false);
		if (val == distance) {
			changeLaneReverse(scope, agent, lane, lane, segment, agentOnLinkedRoad, agentOnLinkedRoad);

			return distance;
		}
		if (val >= distMax) {
			distMax = val;
			newLane = lane;
			newAgentOn = agentOnLinkedRoad;
			onLinkedRoad = true;
		}
		if (lane > 0 && scope.getRandom().next() < probaChangeLaneUp) {
			val = avoidCollision(scope, agent, distance, security_distance, currentLocation, target, lane - 1, segment,
					true, currentRoad, true);
			if (val > distMax && val > 0) {
				distMax = val;
				newLane = lane - 1;
				onLinkedRoad = true;
				newAgentOn = agentOnLinkedRoad;
			}
		}

		// if ( lane != newLane ) {
		if (!onLinkedRoad) {
			setOnLinkedRoad(agent, false);
			changeLanefromReverse(scope, agent, lane, lane, segment, agentOnLinkedRoad, newAgentOn);
		} else {
			changeLaneReverse(scope, agent, lane, lane, segment, agentOnLinkedRoad, newAgentOn);
		}

		// }

		return distMax;
	}
	

	private double avoidCollision(final IScope scope, final IAgent agent, final double distance,
			final double security_distance, final GamaPoint currentLocation, final GamaPoint target, final int lane,
			final int segment, final IAgent currentRoad, final IAgent linkedRoad, final Double probaChangeLaneUp,
			final Double probaChangeLaneDown, final Double probaUseLinkedRoad, final Boolean rightSide) {

		double distMax = 0;
		int newLane = lane;
		final double vl = getVehiculeLength(agent) / 2.0;
		final List agentOn = (List) currentRoad.getAttribute(RoadSkill.AGENTS_ON);
		List newAgentOn = agentOn;
		// boolean changeLane = false;

		if (lane > 0 && scope.getRandom().next() < probaChangeLaneDown) {
			final double val = avoidCollision(scope, agent, distance, security_distance, currentLocation, target,
					lane - 1, segment, false, currentRoad, true);
			if (val == distance) {
				newLane = lane - 1;
				changeLane(scope, agent, lane, newLane, segment, agentOn, agentOn);

				return distance;
			}
			if (val > distMax && val > vl) {
				newLane = lane - 1;
				// changeLane = true;
				distMax = val;
			}
		}

		double val = avoidCollision(scope, agent, distance, security_distance, currentLocation, target, lane, segment,
				false, currentRoad, false);
		if (val == distance) {
			changeLane(scope, agent, lane, lane, segment, agentOn, agentOn);
			return distance;
		}
		if (val >= distMax) {
			distMax = val;
			newLane = lane;
			// changeLane = false;
		}
		if (lane < (Integer) currentRoad.getAttribute(RoadSkill.LANES) - 1
				&& scope.getRandom().next() < probaChangeLaneUp) {
			val = avoidCollision(scope, agent, distance, security_distance, currentLocation, target, lane + 1, segment,
					false, currentRoad, true);
			if (val > distMax && val > vl) {
				distMax = val;
				newLane = lane + 1;
				// changeLane = true;
			}
		}
		boolean onLinkedRoad = false;
		if (linkedRoad != null && scope.getRandom().next() < probaUseLinkedRoad) {
			final int nbLinkedLanes = (Integer) linkedRoad.getAttribute(RoadSkill.LANES);
			val = avoidCollision(scope, agent, distance, security_distance, currentLocation, target, nbLinkedLanes - 1,
					segment, true, currentRoad, true);

			if (val > distMax && val > vl) {
				distMax = val;
				newLane = nbLinkedLanes - 1;
				newAgentOn = (List) linkedRoad.getAttribute(RoadSkill.AGENTS_ON);
				setOnLinkedRoad(agent, true);
				// changeLane = true;
				onLinkedRoad = true;
			}
		}
		// if ( changeLane ) {
		if (onLinkedRoad) {
			changeLanetoReverse(scope, agent, lane, newLane, segment, agentOn, newAgentOn);
		} else {
			changeLane(scope, agent, lane, newLane, segment, agentOn, newAgentOn);
		}
		// }
		return distMax;
	}

	private double moveToNextLocAlongPathOSM(final IScope scope, final IAgent agent, final IPath path,
			final GamaPoint target, final double _distance, final double security_distance, final int lane,
			final IAgent currentRoad, final IAgent linkedRoad, final Double probaChangeLaneUp,
			final Double probaChangeLaneDown, final Double probaUseLinkedRoad, final Boolean rightSide) {
		int currentLane = lane;
		// long t = java.lang.System.currentTimeMillis();
		GamaPoint currentLocation = (GamaPoint) agent.getLocation().copy(scope);
		final GamaPoint falseTarget = target == null ? new GamaPoint(
				currentRoad.getInnerGeometry().getCoordinates()[currentRoad.getInnerGeometry().getCoordinates().length])
				: target;

		/*
		 * final IList indexVals = initMoveAlongPath(agent, path, currentLocation, falseTarget, currentRoad); // t31 +=
		 * java.lang.System.currentTimeMillis() - t; // t = java.lang.System.currentTimeMillis();
		 *
		 * if ( indexVals == null ) { return 0.0; } int indexSegment = (Integer) indexVals.get(0); final int
		 * endIndexSegment = (Integer) indexVals.get(1);
		 */
		int indexSegment = getSegmentIndex(agent);
		final int endIndexSegment = GeometryUtils.getPointsOf(currentRoad).length - 1;

		// if ( indexSegment > endIndexSegment ) { return 0.0; }
		double distance = _distance;
		// final GamaGraph graph = (GamaGraph) path.getGraph();
		double realDistance = 0;
		final IShape line = currentRoad.getGeometry();
		final Coordinate coords[] = line.getInnerGeometry().getCoordinates();
		// final Coordinate coordsLR[] = linkedRoad == null ? null :
		// linkedRoad.getInnerGeometry().getCoordinates();
		// t32 += java.lang.System.currentTimeMillis() - t;
		// long t2 = java.lang.System.currentTimeMillis();
		// DEBUG.OUT(agent + " -> " + currentLocation +"
		// currentRoad: " + currentRoad + " linkedRoad:" + linkedRoad);
		GamaPoint pt = null;
		for (int j = indexSegment; j < endIndexSegment; j++) {
			// t = System.currentTimeMillis();
			final boolean onLinkedRoad = getOnLinkedRoad(agent);
			// if (onLinkedRoad) {
			// pt = new GamaPoint(coordsLR[(j+1)]);
			// DEBUG.OUT("linked road - pt" + pt);

			// } else {
			pt = new GamaPoint(coords[j + 1]);
			// DEBUG.OUT("pt" + pt);
			// }
			final double dist = pt.euclidianDistanceTo(currentLocation);
			setDistanceToGoal(agent, dist);
			// t33 += java.lang.System.currentTimeMillis() - t;
			// t = java.lang.System.currentTimeMillis();
			if (onLinkedRoad) {
				distance = avoidCollisionLinkedRoad(scope, agent, distance, security_distance, currentLocation,
						falseTarget, currentLane, indexSegment, currentRoad, linkedRoad, probaChangeLaneUp,
						probaChangeLaneDown, probaUseLinkedRoad, rightSide);
			} else {
				distance = avoidCollision(scope, agent, distance, security_distance, currentLocation, falseTarget,
						currentLane, indexSegment, currentRoad, linkedRoad, probaChangeLaneUp, probaChangeLaneDown,
						probaUseLinkedRoad, rightSide);
			}
			// t34 += java.lang.System.currentTimeMillis() - t;
			// t = java.lang.System.currentTimeMillis();
			currentLane = (Integer) agent.getAttribute(CURRENT_LANE);
			if (distance < dist) {
				final double ratio = distance / dist;
				final double newX = currentLocation.getX() + ratio * (pt.getX() - currentLocation.getX());
				final double newY = currentLocation.getY() + ratio * (pt.getY() - currentLocation.getY());
				final GamaPoint npt = new GamaPoint(newX, newY);
				realDistance += currentLocation.euclidianDistanceTo(npt);
				currentLocation.setLocation(npt);
				distance = 0;
				// t35 += java.lang.System.currentTimeMillis() - t;
				break;
			}
			currentLocation = pt;
			distance = distance - dist;
			realDistance += dist;
			// t35 += java.lang.System.currentTimeMillis() - t;
			if (j == endIndexSegment) {
				break;
			}
			indexSegment++;
			// setSegmentIndex(agent, indexSegment - 1);
		}
		// t36 += java.lang.System.currentTimeMillis() - t;
		// t = java.lang.System.currentTimeMillis();
		if (pt != null) {
			setDistanceToGoal(agent, pt.distance(currentLocation));
		}
		setLocation(agent, currentLocation);
		if (path != null) {
			path.setSource(currentLocation.copy(scope));
		}

		agent.setAttribute(IKeyword.REAL_SPEED, realDistance);
		// t37 += java.lang.System.currentTimeMillis() - t;
		return _distance == 0.0 ? 1.0 : distance / _distance;
	}
	
	@action (
			name = "die",
			doc = @doc (
					value = "remove the driving agent from its current road and make it die",
					examples = { @example ("do die") }))
	public void primChangeLaneNumber(final IScope scope) throws GamaRuntimeException {
		final AbstractAgent driver = (AbstractAgent) getCurrentAgent(scope);
		if (! driver.dead() && getCurrentRoad(driver) != null) {
			RoadSkill.unregister(driver);
		}
		driver.primDie(scope);
	}

}
