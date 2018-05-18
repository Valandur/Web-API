webpackJsonp([15],{1022:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r,o=n(0),a=(n.n(o),n(72)),i=n(73),s=n(51),c=n(243),l=n(151),p=n(251),u=n(1037),f=n(95),h=this&&this.__extends||(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])},function(e,t){function n(){this.constructor=e}r(e,t),e.prototype=null===t?Object.create(t):(n.prototype=t.prototype,new n)}),d=this&&this.__assign||Object.assign||function(e){for(var t,n=1,r=arguments.length;n<r;n++)for(var o in t=arguments[n])Object.prototype.hasOwnProperty.call(t,o)&&(e[o]=t[o]);return e},m=Object(u.a)("server/properties","key"),y=function(e){function t(){return null!==e&&e.apply(this,arguments)||this}return h(t,e),t.prototype.render=function(){var e=this,t=this.props.t;return o.createElement(o.Fragment,null,!this.props.hideNote&&o.createElement(s.t,{basic:!0},o.createElement(s.o,{info:!0,onDismiss:function(){return e.props.doHideNote()}},o.createElement(s.o.Header,null,t("InfoTitle")),o.createElement("p",null,t("InfoText")))),o.createElement(m,{canEdit:function(t){return Object(f.a)(e.props.perms,["server","properties","modify",t.key])},icon:"cogs",title:t("ServerSettings"),fields:{key:{label:t("Key")},value:{label:t("Value"),view:function(e){return"true"===e.value||"false"===e.value?o.createElement(s.h,{color:"true"===e.value?"green":"red",name:"true"===e.value?"check":"delete"}):e.value},edit:function(e,t){return"true"===e.value||"false"===e.value?o.createElement(s.e.Radio,{toggle:!0,name:"value",checked:"true"===t.state.value,onClick:function(){t.setState({value:"true"===t.state.value?"false":"true"})}}):o.createElement(s.e.Input,{name:"value",type:"text",placeholder:"Value",value:t.state.value,onChange:t.handleChange})}}},onSave:function(t,n,r){e.props.requestSaveProperty(d({},t,{value:n.value})),r.endEdit()}}))},t}(o.Component);t.default=Object(i.b)(function(e){return{perms:e.api.permissions,hideNote:e.preferences.hideServerSettingsNote}},function(e){return{requestSaveProperty:function(t){return e(Object(p.b)(t))},doHideNote:function(){return e(Object(l.b)(c.d.hideServerSettingsNote,!0))}}})(Object(a.c)("ServerSettings")(y))},1033:function(e,t,n){"use strict";var r,o=n(96),a=(n.n(o),n(0)),i=(n.n(a),n(72)),s=n(51),c=n(95),l=n(1034),p=n(1035),u=n(1036),f=this&&this.__extends||(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])},function(e,t){function n(){this.constructor=e}r(e,t),e.prototype=null===t?Object.create(t):(n.prototype=t.prototype,new n)}),h=this&&this.__assign||Object.assign||function(e){for(var t,n=1,r=arguments.length;n<r;n++)for(var o in t=arguments[n])Object.prototype.hasOwnProperty.call(t,o)&&(e[o]=t[o]);return e},d=function(e){function t(t){var n=e.call(this,t)||this;return n.state={page:0,newData:{}},n.changePage=n.changePage.bind(n),n.doHandleChange=n.doHandleChange.bind(n),n.handleChange=c.f.bind(n,n.doHandleChange),n}return f(t,e),t.prototype.doHandleChange=function(e,t){var n;this.setState({newData:h({},this.state.newData,(n={},n[e]=t,n))})},t.prototype.changePage=function(e,t){e.preventDefault(),this.setState({page:t})},t.prototype.onEdit=function(e,t){var n=this,r={};e&&Object.keys(this.props.fields).forEach(function(t){n.props.fields[t].edit&&(r[t]=o.get(e,t))}),this.setState({newData:r}),this.props.onEdit&&this.props.onEdit(e,t)},t.prototype.shouldComponentUpdate=function(e,t){return e.fields!==this.props.fields||e.list!==this.props.list||t.page!==this.state.page||t.newData!==this.state.newData},t.prototype.render=function(){var e=this,t=this.props,n=t.icon,r=t.title,o=t.list,i=t.canEdit,c=t.canDelete,f=t.actions,d=Object.keys(this.props.fields).map(function(t){return e.props.fields[t]}).filter(function(e){return e.view}),m=Math.ceil(o.length/20),y=Math.min(this.state.page,m-1),g=o.slice(20*y,20*(y+1)),v={handleChange:this.handleChange,state:this.state.newData,setState:function(t){return e.setState({newData:h({},e.state.newData,t)})}},b=this.props.t;return a.createElement("div",{style:{marginTop:"2em"}},r&&a.createElement(s.g,null,a.createElement(s.h,{fitted:!0,name:n})," ",r),a.createElement(s.w,{striped:!0,stackable:!0},a.createElement(p.a,{fields:d,hasActions:"undefined"!==typeof f,canEdit:!!i,canDelete:!!c,t:b}),a.createElement(s.w.Body,null,g.map(function(t,n){return a.createElement(u.a,{key:e.props.idFunc(t),obj:t,tableRef:v,canEdit:i,canDelete:c,editing:e.props.isEditing(t),fields:d,onEdit:function(t,n){return e.onEdit(t,n)},onSave:e.props.onSave,onDelete:e.props.onDelete,actions:e.props.actions,newData:e.state.newData,handleChange:e.handleChange,t:b})}))),a.createElement(l.a,{page:y,maxPage:m,changePage:function(t,n){return e.changePage(t,n)}}))},t}(a.Component);t.a=Object(i.c)("DataTable")(d)},1034:function(e,t,n){"use strict";var r,o=n(0),a=(n.n(o),n(51)),i=this&&this.__extends||(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])},function(e,t){function n(){this.constructor=e}r(e,t),e.prototype=null===t?Object.create(t):(n.prototype=t.prototype,new n)}),s=function(e){function t(){return null!==e&&e.apply(this,arguments)||this}return i(t,e),t.prototype.shouldComponentUpdate=function(e,t){return e.page!==this.props.page||e.maxPage!==this.props.maxPage},t.prototype.render=function(){var e=this;if(this.props.maxPage<=1)return null;for(var t=this.props,n=t.page,r=t.maxPage,i=Math.max(0,n-4),s=Math.min(r,n+5),c=[],l=i;l<s;l++)c.push(l);return o.createElement(a.n,{pagination:!0},n>4?o.createElement(a.n.Item,{onClick:function(t){return e.props.changePage(t,0)}},"1"):null,n>5?o.createElement(a.n.Item,{onClick:function(t){return e.props.changePage(t,n-5)}},"..."):null,c.map(function(t){return o.createElement(a.n.Item,{key:t,onClick:function(n){return e.props.changePage(n,t)},active:t===n},t+1)}),n<r-6?o.createElement(a.n.Item,{onClick:function(t){return e.props.changePage(t,n+5)}},"..."):null,n<r-5?o.createElement(a.n.Item,{onClick:function(t){return e.props.changePage(t,r-1)}},r):null)},t}(o.Component);t.a=s},1035:function(e,t,n){"use strict";var r,o=n(0),a=(n.n(o),n(51)),i=this&&this.__extends||(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])},function(e,t){function n(){this.constructor=e}r(e,t),e.prototype=null===t?Object.create(t):(n.prototype=t.prototype,new n)}),s=function(e){function t(){return null!==e&&e.apply(this,arguments)||this}return i(t,e),t.prototype.shouldComponentUpdate=function(e,t){return e.fields!==this.props.fields},t.prototype.render=function(){var e=this;return o.createElement(a.w.Header,null,o.createElement(a.w.Row,null,Object.keys(this.props.fields).map(function(t){var n=e.props.fields[t];return o.createElement(a.w.HeaderCell,{key:t},n.label?n.label:"<"+n.name+">")}),this.props.hasActions||this.props.canEdit||this.props.canDelete?o.createElement(a.w.HeaderCell,null,this.props.t("Actions")):null))},t}(o.Component);t.a=s},1036:function(e,t,n){"use strict";var r,o=n(0),a=(n.n(o),n(51)),i=n(95),s=this&&this.__extends||(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])},function(e,t){function n(){this.constructor=e}r(e,t),e.prototype=null===t?Object.create(t):(n.prototype=t.prototype,new n)}),c=function(e){function t(){return null!==e&&e.apply(this,arguments)||this}return s(t,e),t.prototype.shouldComponentUpdate=function(e,t){return e.obj!==this.props.obj||e.editing!==this.props.editing||e.fields!==this.props.fields||this.props.editing&&e.newData!==this.props.newData},t.prototype.renderEdit=function(e,t){return t.options?o.createElement(a.e.Field,{fluid:!0,selection:!0,search:!0,control:a.d,name:t.name,placeholder:t.label,options:t.options,value:this.props.newData[t.name],onChange:this.props.handleChange}):o.createElement(a.e.Input,{name:t.name,type:t.type?t.type:"text",placeholder:t.label,value:this.props.newData[t.name],onChange:this.props.handleChange})},t.prototype.render=function(){var e=this,t=this.props,n=t.actions,r=t.fields,s=t.obj,c=t.canEdit,l=t.canDelete,p=t.editing,u=t.tableRef,f="function"===typeof c?c(s):c,h="function"===typeof l?l(s):c;return o.createElement(a.w.Row,null,r.map(function(t,n){return o.createElement(a.w.Cell,{key:n,collapsing:!t.wide},t.edit&&p?"function"===typeof t.edit?t.edit(s,u):e.renderEdit(s,t):"function"===typeof t.view?t.view(s,u):Object(i.e)(s,t.name))}),n||c||l?o.createElement(a.w.Cell,{collapsing:!0},f&&p?[o.createElement(a.b,{key:"save",primary:!0,disabled:s.updating,loading:s.updating,onClick:function(){e.props.onSave&&e.props.onSave(s,e.props.newData,u)}},o.createElement(a.h,{name:"save"})," ",this.props.t("Save")),o.createElement(a.b,{key:"cancel",secondary:!0,disabled:s.updating,loading:s.updating,onClick:function(){return e.props.onEdit(null,u)}},o.createElement(a.h,{name:"cancel"})," ",this.props.t("Cancel"))]:f?o.createElement(a.b,{primary:!0,disabled:s.updating,loading:s.updating,onClick:function(){return e.props.onEdit(s,u)}},o.createElement(a.h,{name:"edit"})," ",this.props.t("Edit")):null,h&&o.createElement(a.b,{negative:!0,disabled:s.updating,loading:s.updating,onClick:function(){e.props.onDelete&&e.props.onDelete(s,u)}},o.createElement(a.h,{name:"trash"})," ",this.props.t("Remove")),n&&n(s,u)):null)},t}(o.Component);t.a=c},1037:function(e,t,n){"use strict";t.a=function(e,t,n){t||(t="id");var r="function"===typeof t?t:function(e){return o.get(e,t)};return Object(i.b)(function(e,t){return function(n,r){var a=o.get(n,e.replace(/\//g,"_").replace("-",""));return{creating:!!a&&a.creating,filter:a&&a.filter?a.filter:{},list:a?a.list:[],types:n.api.types,idFunc:t,perm:e.split("/"),perms:n.api.permissions}}}(e,r),function(e,t,n){return function(r){return{requestList:function(){return r(Object(c.f)(e,!n))},requestDetails:function(n){return r(Object(c.e)(e,t,n))},requestCreate:function(n){return r(Object(c.c)(e,t,n))},requestChange:function(n,o){return r(Object(c.b)(e,t,n,o))},requestDelete:function(n){return r(Object(c.d)(e,t,n))},setFilter:function(t,n){return r(Object(c.l)(e,t,n))},equals:function(e,n){return null!=e&&null!=n&&t(e)===t(n)}}}}(e,r,!!n))(m)};var r,o=n(96),a=(n.n(o),n(0)),i=(n.n(a),n(73)),s=n(51),c=n(150),l=n(1038),p=n(1033),u=n(1039),f=n(95),h=this&&this.__extends||(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])},function(e,t){function n(){this.constructor=e}r(e,t),e.prototype=null===t?Object.create(t):(n.prototype=t.prototype,new n)}),d=this&&this.__assign||Object.assign||function(e){for(var t,n=1,r=arguments.length;n<r;n++)for(var o in t=arguments[n])Object.prototype.hasOwnProperty.call(t,o)&&(e[o]=t[o]);return e},m=function(e){function t(t){var n=e.call(this,t)||this;return n.state={page:0,data:void 0},n.details=n.details.bind(n),n.create=n.create.bind(n),n.edit=n.edit.bind(n),n.endEdit=n.endEdit.bind(n),n.save=n.save.bind(n),n.delete=n.delete.bind(n),n}return h(t,e),t.prototype.createTable=function(){return p.a},t.prototype.componentDidMount=function(){this.props.static||(this.props.requestList(),this.interval=setInterval(this.props.requestList,1e4))},t.prototype.componentWillUnmount=function(){this.interval&&clearInterval(this.interval)},t.prototype.shouldComponentUpdate=function(e,t){return e.creating!==this.props.creating||e.filter!==this.props.filter||e.fields!==this.props.fields||e.list!==this.props.list||t.data!==this.state.data},t.prototype.create=function(e){this.props.requestCreate(e)},t.prototype.details=function(e){this.props.requestDetails(e)},t.prototype.edit=function(e){this.setState({data:e})},t.prototype.save=function(e,t){this.props.requestChange(e,t),this.endEdit()},t.prototype.endEdit=function(){this.setState({data:void 0})},t.prototype.delete=function(e){this.props.requestDelete(e)},t.prototype.render=function(){var e=this,t=this.props,n=t.filter,r=t.canEdit,i=t.canDelete,c=t.onCreate,p=t.onEdit,h=t.onDelete,m=t.onSave,y=t.perm,g=t.perms,v=t.title,b=t.createTitle,E=t.filterTitle,_=t.checkCreatePerm,C=[],O=!1,w=function(t){return d({},t,{create:e.create,details:e.details,save:e.save,edit:e.edit,endEdit:e.endEdit,delete:e.delete})},j=o.mapValues(this.props.fields,function(e,t){var n={name:t,view:!0};if("string"===typeof e)n.label=e;else if("function"===typeof e)n.view=function(t,n){return e(t,w(n))};else if("object"===typeof e&&(n=d({},n,e),"function"===typeof e.view)){var r=e.view;n.view=function(e,t){return r(e,w(t))}}return n}),D={},k={};Object.keys(j).forEach(function(e){j[e].create&&(D[e]=j[e]),j[e].filter&&(k[e]=j[e])});try{Object.keys(n).forEach(function(e){var t=n[e],r=Object.keys(k).map(function(e){return k[e]}).find(function(t){return t.filterName===e})||k[e],a=function(t){return o.get(t,e)};if("function"===typeof r.filterValue&&(a=r.filterValue),o.isArray(t)){if(0===t.length)return;C.push(function(e){var n=a(e);return t.indexOf(n)>=0})}else C.push(function(e){return new RegExp(t,"i").test(a(e))})}),O=!0}catch(e){O=!1}var P=this.props.list.filter(function(e){return!O||C.every(function(t){return t(e)})}),S=b&&E?2:1,q=this.props.actions,x=q;"function"===typeof q&&(x=function(e,t){return q(e,w(t))});var F=this.createTable();return a.createElement(s.t,{basic:!0},a.createElement(s.f,{stackable:!0,doubling:!0,columns:S},b&&(!_||Object(f.a)(g,y.concat("create")))&&a.createElement(s.f.Column,null,a.createElement(l.a,{title:b,button:this.props.createButton,creating:this.props.creating,fields:D,onCreate:function(t,n){return c?c(t,w(n)):e.create(t)}})),E&&a.createElement(s.f.Column,null,a.createElement(u.a,{title:E,fields:k,valid:O,values:n,onFilterChange:this.props.setFilter}))),a.createElement(F,{title:v,icon:this.props.icon,list:P,t:this.props.t,idFunc:this.props.idFunc,fields:j,onEdit:function(t,n){return p?p(t,w(n)):e.edit(t)},onSave:function(t,n,r){return m?m(t,n,w(r)):e.save(t,n)},onDelete:function(t,n){return h?h(t,w(n)):e.delete(t)},canEdit:function(e){return("function"===typeof r?r(e):!!r)&&Object(f.a)(g,y.concat("modify"))},canDelete:function(e){return("function"===typeof i?i(e):!!i)&&Object(f.a)(g,y.concat("delete"))},actions:x,isEditing:function(t){return e.props.equals(t,e.state.data)}}))},t}(a.Component)},1038:function(e,t,n){"use strict";var r,o=n(96),a=(n.n(o),n(0)),i=(n.n(a),n(72)),s=n(51),c=n(95),l=this&&this.__extends||(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])},function(e,t){function n(){this.constructor=e}r(e,t),e.prototype=null===t?Object.create(t):(n.prototype=t.prototype,new n)}),p=this&&this.__assign||Object.assign||function(e){for(var t,n=1,r=arguments.length;n<r;n++)for(var o in t=arguments[n])Object.prototype.hasOwnProperty.call(t,o)&&(e[o]=t[o]);return e},u=function(e){function t(t){var n=e.call(this,t)||this;return n.state={open:!1,newData:{}},n.doHandleChange=n.doHandleChange.bind(n),n.handleChange=c.f.bind(n,n.doHandleChange),n.create=n.create.bind(n),n}return l(t,e),t.prototype.doHandleChange=function(e,t){var n;this.setState({newData:p({},this.state.newData,(n={},n[e]=t,n))})},t.prototype.shouldComponentUpdate=function(e,t){return e.creating!==this.props.creating||e.fields!==this.props.fields||t.newData!==this.state.newData||t.open!==this.state.open},t.prototype.create=function(){var e=this,t={};Object.keys(this.state.newData).forEach(function(n){o.set(t,n,e.state.newData[n])}),this.props.onCreate(t,{state:this.state.newData,setState:this.setState,handleChange:this.handleChange})},t.prototype.canCreate=function(){var e=this;return Object.keys(this.props.fields).every(function(t){var n=e.props.fields[t],r=n.createName?n.createName:t;return"string"===typeof n||!n.required||e.state.newData[r]})},t.prototype.handleClick=function(){this.setState({open:!this.state.open})},t.prototype.render=function(){var e=this,t=this.props,n=t.title,r=t.creating,o=t.fields,i=this.props.t,c=[];return Object.keys(o).forEach(function(e){var t=o[e],n=p({},t,{name:t.createName?t.createName:e});n.isGroup?c.push({only:n}):c.length&&!c[c.length-1].second?c[c.length-1].second=n:c.push({first:n})}),a.createElement(s.a,{styled:!0,fluid:!0},a.createElement(s.a.Title,{active:this.state.open,onClick:function(){return e.handleClick()}},a.createElement(s.h,{fitted:!0,name:"plus"})," ",n),a.createElement(s.a.Content,{active:this.state.open},a.createElement(s.e,{loading:r},c.map(function(t,n){return t.only?a.createElement("div",{key:n},e.renderField(t.only)):a.createElement(s.e.Group,{key:n,widths:"equal"},t.first&&e.renderField(t.first),t.second&&e.renderField(t.second))}),a.createElement(s.b,{primary:!0,onClick:this.create,disabled:!this.canCreate()},this.props.button||i("Create")))))},t.prototype.renderField=function(e){var t=this.state.newData;return"function"===typeof e.create?e.create({state:t,setState:this.setState,handleChange:this.handleChange,value:t[e.name]}):e.options?a.createElement(s.e.Field,{fluid:!0,selection:!0,search:!0,required:e.required,control:s.d,name:e.name,label:e.label,placeholder:e.label,onChange:this.handleChange,value:t[e.name],options:e.options}):a.createElement(s.e.Input,{required:e.required,type:e.type?e.type:"text",name:e.name,label:e.label,placeholder:e.label,onChange:this.handleChange,value:t[e.name]})},t}(a.Component);t.a=Object(i.c)("CreateForm")(u)},1039:function(e,t,n){"use strict";var r,o=n(96),a=(n.n(o),n(0)),i=(n.n(a),n(51)),s=n(95),c=this&&this.__extends||(r=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var n in t)t.hasOwnProperty(n)&&(e[n]=t[n])},function(e,t){function n(){this.constructor=e}r(e,t),e.prototype=null===t?Object.create(t):(n.prototype=t.prototype,new n)}),l=this&&this.__assign||Object.assign||function(e){for(var t,n=1,r=arguments.length;n<r;n++)for(var o in t=arguments[n])Object.prototype.hasOwnProperty.call(t,o)&&(e[o]=t[o]);return e},p=function(e){function t(t){var n=e.call(this,t)||this;return n.state={open:!1},n.handleChange=s.f.bind(n,n.props.onFilterChange),n}return c(t,e),t.prototype.shouldComponentUpdate=function(e,t){return e.values!==this.props.values||e.fields!==this.props.fields||e.valid!==this.props.valid||t.open!==this.state.open},t.prototype.handleClick=function(){this.setState({open:!this.state.open})},t.prototype.render=function(){var e=this,t=this.props,n=t.title,r=t.fields,s=t.values,c=t.valid,p=[];return Object.keys(r).forEach(function(e){var t=r[e],n=l({},t,{name:t.filterName?t.filterName:e});n.isGroup?p.push({only:n}):p.length&&!p[p.length-1].second?p[p.length-1].second=n:p.push({first:n})}),a.createElement(i.a,{styled:!0,fluid:!0},a.createElement(i.a.Title,{active:this.state.open,onClick:function(){return e.handleClick()}},a.createElement(i.h,{name:"filter",fitted:!0})," ",n),a.createElement(i.a.Content,{active:this.state.open},a.createElement(i.e,null,p.map(function(t,n){return t.only?e.renderField(t.only,o.get(s,t.only.name),!c):a.createElement(i.e.Group,{key:n,widths:"equal"},t.first&&e.renderField(t.first,o.get(s,t.first.name),!c),t.second&&e.renderField(t.second,o.get(s,t.second.name),!c))}),a.createElement(i.o,{error:!0,visible:!c,content:"Search term must be a valid regex"}))))},t.prototype.renderField=function(e,t,n){return"function"===typeof e.filter?e.filter({state:this.props.values,setState:this.setState,handleChange:this.handleChange,value:t}):e.options?(t||(t=[]),a.createElement(i.e.Field,{fluid:!0,selection:!0,search:!0,multiple:!0,control:i.d,name:e.name,label:e.label,placeholder:e.label,options:e.options,value:t,error:n,onChange:this.handleChange})):a.createElement(i.e.Input,{name:e.name,type:e.type?e.type:"text",label:e.label,placeholder:e.label,value:t,error:n,onChange:this.handleChange})},t}(a.Component);t.a=p}});
//# sourceMappingURL=15.b13ca54d.chunk.js.map