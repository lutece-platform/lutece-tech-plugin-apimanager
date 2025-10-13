function getMenuTemplate(){

    return (
        "<div class=\"navbar navbar-expand-lg bg-body-tertiary\" id=\"right-list\">\n" +
        "  <div class=\"container-fluid\">\n" +
        "    <div class=\"collapse navbar-collapse\" id=\"navbarSupportedContent\">\n" +
        "      <ul class=\"navbar-nav me-auto mb-2 mb-lg-0\">\n" +
        "        <li class=\"nav-item dropdown\">\n" +
        "          <a class=\"nav-link dropdown-toggle\" href=\"#\" role=\"button\" data-bs-toggle=\"dropdown\" aria-expanded=\"false\">\n" +
        "            Admin\n" +
        "          </a>\n" +
        "          <ul class=\"dropdown-menu\">\n" +
        "          </ul>\n" +
        "        </li>\n"+
        "          <li id=\"child-menu-more\" class=\"nav-item dropdown list-unstyled d-none\" style=\"position:static !important;\">\n" +
        "          <a class=\"nav-link\" id=\"child-menu-more-btn\" href=\"#\" role=\"button\" data-bs-toggle=\"dropdown\" data-bs-boundary=\"viewport\" aria-expanded=\"false\" title=\"Autre(s)\">\n" +
        "            <h3><i class=\"ti ti-dots fs-2\"></i></h3>\n" +
        "          </a>\n" +
        "          <ul class=\"dropdown-menu dropdown-menu-end\" aria-labelledby=\"navbarDropdown\"></ul>\n" +
        "        </li>"+
        "      </ul>\n" +
        "    </div>\n" +
        "  </div>\n" +
        "</div>\n"
    );
}

const adminMenu=["jsp/admin/plugins/apimanager/ManageMeecrogates.jsp","jsp/admin/plugins/apimanager/ManagePlans.jsp",
    "jsp/admin/plugins/apimanager/ManageInstances.jsp","jsp/admin/plugins/apimanager/ManageOperations.jsp"];
const menuItems = [];
const template = getMenuTemplate();
$("#child-menu .list-group-item").each(function () {
    if(!$(this).hasClass("d-none")
        && $(this).attr("feature-url") != null
        && $(this).attr("feature-url").indexOf("jsp/admin/plugins/apimanager") != -1 ){


        menuItems.push($(this));
    }
})
const templateMenu =  $(template);
const adminMmenu =templateMenu.find(".dropdown-menu");
const menu = templateMenu.find(".navbar-nav");
const currentPageHref = document.location.href;
menuItems.reverse().forEach(function (item) {
    const isCurrentPage = (currentPageHref.indexOf($(item).attr("href"))!==-1);
    if(adminMenu.includes($(item).attr("feature-url"))){
        $(adminMmenu).append("<li ><a class=\"dropdown-item "+(isCurrentPage?"active":"")+"\" href=\""+$(item).attr("href")+"\">"+$(item).attr("title")+"</a></li>\n" );
    }else{
        $(menu).prepend("<li class=\"nav-item\"><a class=\"nav-link "+(isCurrentPage?"active":"")+"\" href=\""+$(item).attr("href")+"\">"+$(item).attr("title")+"</a></li>\n" );
    }
});


$("#right-list").replaceWith(templateMenu);
$("#child-menu").removeClass("d-none");
$("#child-menu").css("overflow","visible");
$("#child-menu .content").attr("style","overflow:visible !important");
