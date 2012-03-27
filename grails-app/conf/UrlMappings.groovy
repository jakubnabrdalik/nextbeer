class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')

        "/openapi/propose"(controller: "openapi", parseRequest: true) {
            action = [GET: "propose", PUT: "propose", DELETE: "propose", POST: "propose"]
        }
	}
}
