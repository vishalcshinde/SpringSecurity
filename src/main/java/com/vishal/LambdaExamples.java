package demo;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import org.junit.Test;

public class LambdaExamples {
	List<Dish> menu = Arrays.asList(new Dish("pork", false, 800, Dish.Type.MEAT),
			new Dish("beef", false, 700, Dish.Type.MEAT), new Dish("chicken", false, 400, Dish.Type.MEAT),
			new Dish("french fries", true, 530, Dish.Type.OTHER), new Dish("rice", true, 350, Dish.Type.OTHER),
			new Dish("season fruit", true, 120, Dish.Type.OTHER), new Dish("pizza", true, 550, Dish.Type.OTHER),
			new Dish("prawns", false, 300, Dish.Type.FISH), new Dish("salmon", false, 450, Dish.Type.FISH));
	
	@Test
	public void testGroupBy() throws Exception {
		System.out.println(menu.stream().collect(groupingBy(Dish::getType)));
	}
	
	@Test
	public void testLimit() throws Exception {
		menu.stream().forEach(System.out::println);
		System.out.println(menu.stream().filter(i -> i.getCalories() > 300).limit(3).collect(toList()));
		Arrays.asList(1,2,3,4,5,6,7,8,1,2,3,5,6).stream().filter(i -> i % 2 == 0).distinct().forEach(System.out::println);;
		System.out.println(Arrays.asList(1,2,3,4,5,6,7,8,1,2,3,5,6).stream().reduce(Integer::max));
		
		
	}
	
	@Test
	public void testStream() throws Exception {
		List<Account> accounts =  getAccounts().limit(1000000).collect(toList());
		long i = System.currentTimeMillis();
		Simulation sim = accounts
				.parallelStream()
				.reduce(new Simulation(), (simulation, account) -> {
					IncludedAccount includedAccount = new IncludedAccount();
					includedAccount.account = account;
					account.holdings.forEach(h -> {
						IncludedHolding includedHolding = new IncludedHolding();
						includedHolding.holding = h;
						includedHolding.includedAccount = includedAccount;
						includedAccount.includedHoldings.add(includedHolding);
					});
					simulation.accounts.add(account);
					simulation.includedAccounts.add(includedAccount);
					return simulation;
			}, (a, b) -> {
				
				a.accounts.addAll(b.accounts);
				a.includedAccounts.addAll(b.includedAccounts);
				return b;
			});
		
		System.out.println(sim.includedAccounts.size());
		System.out.println(System.currentTimeMillis() - i);
	}
	
	@Test
	public void testStream2() throws Exception {
		List<Account> accounts =  getAccounts().limit(1000000).collect(toList());
		long i = System.currentTimeMillis();
		Accumulator acc= new Accumulator();
		accounts
			.stream()
			.parallel()
			.forEach(acc::add);
	
		System.out.println(acc.simulation.includedAccounts.size());
		System.out.println(System.currentTimeMillis() - i);
	}
	
	@Test
	public void testParallel() throws Exception {
		long curr = System.currentTimeMillis();
		System.out.println(LongStream.rangeClosed(1, 10_000_000)
									.parallel()
									//.peek(i -> {System.out.println(Thread.currentThread().getName());})
									.reduce(0l, Long::sum));
		System.out.println(System.currentTimeMillis() - curr);
		System.out.println(Runtime.getRuntime().availableProcessors());
	}
	
	class Accumulator {
		Simulation simulation = new Simulation();
		
		public void add(Account account) {
			IncludedAccount includedAccount = new IncludedAccount();
			includedAccount.account = account;
			account.holdings.forEach(h -> {
				IncludedHolding includedHolding = new IncludedHolding();
				includedHolding.holding = h;
				includedHolding.includedAccount = includedAccount;
				includedAccount.includedHoldings.add(includedHolding);
			});
			simulation.accounts.add(account);
			simulation.includedAccounts.add(includedAccount);
		}
	}
	
	@Test
	public void testForkJoin() throws Exception {
		System.out.println(999 / 4);
		System.out.println(999 % 4);
		List<Account> accounts =  getAccounts().limit(10000).collect(toList());
		long curr = System.currentTimeMillis();
		int batchsize = accounts.size() / 4;
		List<Account> firstBatch = accounts.stream().limit(batchsize).collect(toList());
		List<Account> secondBatch = accounts.stream().skip(batchsize).limit(batchsize).collect(toList());
		List<Account> thirdBatch = accounts.stream().skip(batchsize * 2).limit(batchsize).collect(toList());
		List<Account> fourthdBatch = accounts.stream().skip(batchsize * 3).collect(toList());
		SimualtionCreator create1 = new SimualtionCreator(firstBatch);
		SimualtionCreator create2 = new SimualtionCreator(secondBatch);
		SimualtionCreator create3 = new SimualtionCreator(thirdBatch);
		SimualtionCreator create4 = new SimualtionCreator(fourthdBatch);
		
		List<Future<Simulation>> futures = new ForkJoinPool(4).invokeAll(Arrays.asList(create1, create2, create3, create4));
		System.out.println(System.currentTimeMillis() - curr);
		System.out.println(futures.get(0).get().includedAccounts.size());
		System.out.println(futures.get(1).get().includedAccounts.size());
		System.out.println(futures.get(2).get().includedAccounts.size());
		System.out.println(futures.get(3).get().includedAccounts.size());
		
 	}
	
	
	@Test
	public void testForkJoin1() throws Exception {
		List<Account> accounts =  getAccounts().limit(10).collect(toList());
		long curr = System.currentTimeMillis();
		SimualtionCreator create1 = new SimualtionCreator(accounts);
		List<Future<Simulation>> futures = new ForkJoinPool().invokeAll(Arrays.asList(create1));
		System.out.println(System.currentTimeMillis() - curr);
		System.out.println(futures.get(0).get().includedAccounts.size());
		
 	}
	
	@SuppressWarnings("serial")
	class SimualtionCreator implements Callable<Simulation> {
		
		List<Account> accounts;
		
		SimualtionCreator(List<Account> accounts) {
			this.accounts = accounts;
		}


		@Override
		public Simulation call() throws Exception {
			// TODO Auto-generated method stub
			 return accounts
					.stream()
					.reduce(new Simulation(), (simulation, account) -> {
						try {
							Thread.sleep(100);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						IncludedAccount includedAccount = new IncludedAccount();
						includedAccount.account = account;
						account.holdings.forEach(h -> {
							IncludedHolding includedHolding = new IncludedHolding();
							includedHolding.holding = h;
							includedHolding.includedAccount = includedAccount;
							includedAccount.includedHoldings.add(includedHolding);
						});
						simulation.accounts.add(account);
						simulation.includedAccounts.add(includedAccount);
						return simulation;
				}, (a, b) -> {
					
					a.accounts.addAll(b.accounts);
					a.includedAccounts.addAll(b.includedAccounts);
					return b;
				});
		}
		
	}
	
	
	Stream<Account> getAccounts() {
		return Stream.generate(new Supplier<Account>() {
			int i = 0;	
			@Override
			public Account get() {
				i = i +1;
				Account acc= new Account();
				acc.name = "test " + 1;
				Holding holding = new Holding();
				holding.name = "holding1";
				acc.holdings.add(holding);
				holding = new Holding();
				holding.name = "holding2";
				acc.holdings.add(holding);holding = new Holding();
				holding.name = "holding3";
				acc.holdings.add(holding);holding = new Holding();
				holding.name = "holding4";
				acc.holdings.add(holding);
				return acc;
			}
			
		});
	}
}

class Simulation{
	List<Account> accounts = new LinkedList<>();
	List<IncludedAccount> includedAccounts = new LinkedList<>();
	@Override
	public String toString() {
		return "Simulation [accounts=" + accounts + ", includedAccounts=" + includedAccounts + "]\n";
	}
	
}
class Account {
	List<Holding> holdings = new LinkedList<>();
	String name;
}

class Holding {
	String name;
}

class IncludedAccount {
	List<IncludedHolding> includedHoldings = new LinkedList<>();
	Account account;
}

class IncludedHolding {
	IncludedAccount includedAccount;
	Holding holding; 
}

class SimulationAccount {
	
}

class SImulationHolding {
	
}

class Dish {
	private final String name;
	private final boolean vegetarian;
	private final int calories;
	private final Type type;

	public Dish(String name, boolean vegetarian, int calories, Type type) {
		this.name = name;
		this.vegetarian = vegetarian;
		this.calories = calories;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public boolean isVegetarian() {
		return vegetarian;
	}

	public int getCalories() {
		return calories;
	}

	public Type getType() {
		return type;
	}

	public String toString() {
		return name;
	}

	public enum Type {
		MEAT, FISH, OTHER
	}
}
