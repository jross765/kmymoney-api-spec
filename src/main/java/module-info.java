module kmymoney.apispec {

	requires static org.slf4j;
	requires java.desktop;
	
	// ----------------------------

	requires transitive schnorxoborx.schnorxolib;

	requires transitive kmymoney.base;
	requires transitive kmymoney.api;

	// ----------------------------

	exports org.kmymoney.apispec.read;
	exports org.kmymoney.apispec.read.impl;
	
	exports org.kmymoney.apispec.write;
	exports org.kmymoney.apispec.write.impl;

}
