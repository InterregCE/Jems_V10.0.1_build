import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {catchError, share, take, tap} from 'rxjs/operators';
import {Log} from '@common/utils/log';
import {Observable, of} from 'rxjs';

@Injectable({providedIn: 'root'})
export class DownloadService {
  constructor(private httpClient: HttpClient) {
  }

  download(url: string, defaultFilename: string): Observable<HttpResponse<Blob> | null> {
    const result = this.httpClient.get<Blob>(url, {
      observe: 'response',
      responseType: 'blob' as 'json'
    }).pipe(
      take(1),
      tap(response => {
          const binaryData = [response?.body as Blob];
          const objectUrl = window.URL.createObjectURL(new Blob(binaryData));
          const link = document.createElement('a');
          const contentDispositionHeader = response?.headers.get('content-disposition');
          const indexOfFilenameLabel = contentDispositionHeader?.indexOf('filename=') || -1;
          const filename = indexOfFilenameLabel ? contentDispositionHeader?.substr(indexOfFilenameLabel + 10, contentDispositionHeader?.length - indexOfFilenameLabel - 11) || defaultFilename : defaultFilename;
          document.body.appendChild(link);
          link.setAttribute('style', 'display: none');
          link.setAttribute('target', 'blank');
          link.href = objectUrl;
          link.download = filename;
          link.click();
          window.URL.revokeObjectURL(objectUrl);
          link.remove();
      }),
      catchError((error: HttpErrorResponse) => {
          Log.error('Failed to download the file', error);
          return of(null);
        }
      ),
      share()
    );
    result.subscribe();
    return result;
  }
}
