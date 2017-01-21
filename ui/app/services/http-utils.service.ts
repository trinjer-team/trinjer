import {Injectable, OnInit} from "@angular/core";
import {Http, RequestOptions, Headers, Response} from "@angular/http";
import {TokenService} from "./token.service";
import "rxjs/Rx";
import {Observable} from "rxjs/Rx";
import {UserDTO} from "../models/user.dto.interface";
import {IProject} from "../project/project.interface";
/**
 * Created by Andrew Zelenskiy on 09.01.2017.
 */

@Injectable()
export class HttpUtils{
    private baseUrl: string = "http://localhost:8080/api/";
    private optionsWithoutToken: RequestOptions;
    private options: RequestOptions;


    makePost(prefix: string, body: Object, params: Object = {}): Observable<any> {
        if(this.options === null)throw new Error("Options are not exist!");

        return this.http.post(this.baseUrl + prefix, body, this.getOptions(params));
    }


    makeGet(prefix: string, options: Object = {}):Observable<Response> {
        return this.http.get(this.baseUrl + prefix, this.getOptions(options));
    }


    makePostWithoutToken(prefix: string, body: Object, params: Object = {}): Observable<any>{
        return this.http.post(this.baseUrl + prefix, body, this.getOptions(params));
    }


    private getOptions(options: Object): RequestOptions{
        let result = new RequestOptions();
        Object.assign(result, this.options, options);

        //noinspection TypeScriptValidateTypes
        return result;
    }


    constructor(
        private http: Http,
        private tokenService: TokenService
    ){
        let defaultHeaders: Headers = new Headers({'Content-Type': ' x-www-url-encoded'});
        this.optionsWithoutToken = new RequestOptions({headers: defaultHeaders});

        this.tokenService.token.subscribe(this.initializeTokenOptions);
    }

    initializeTokenOptions(token: string){
        let headers = new Headers();

        headers.append('content-type', 'x-www-url-encoded');
        headers.append('x-auth-token', token);

        this.options = new RequestOptions({
            headers: headers
        });
    }
}