

fun buildResponse(d={}) = do {
	var customer_name_prop = "customer.name"
	---
	{
		customer: {
			zip: Mule::p("customer.zip_default"),
			first_name: p(customer_name_prop)
		}
	}
}
