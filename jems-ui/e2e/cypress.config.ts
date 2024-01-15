import {defineConfig} from 'cypress';
import { cloudPlugin } from 'cypress-cloud/plugin';
import xlsx from 'node-xlsx';
import comparePdf from 'compare-pdf';

export default defineConfig({
  defaultCommandTimeout: 8000,
  projectId: 'JEMS',
  viewportHeight: 720,
  viewportWidth: 1280,
  watchForFileChanges: false,
  chromeWebSecurity: false,
  retries: {
    runMode: 1,
    openMode: 0,
  },
  env: {
    authenticationUrl: '/api/auth/login',
    defaultPassword: '<change-me>',
  },
  e2e: {
    baseUrl: 'http://localhost:4200/',
    specPattern: ['cypress/e2e/login.spec.ts', 'cypress/e2e/programme.spec.ts', 'cypress/e2e/**/*.spec.ts'],
    async setupNodeEvents(on, config) {
      on('task', {
        async parseXLSX(subject) {
          return xlsx.parse(Buffer.from(subject.data));
        },
      });

      on('task', {
        comparePdf({templatePdf, actualPdf, masks, baselinePath}) {
          let config = {
            paths: {
              actualPdfRootFolder: 'cypress/downloads',
              baselinePdfRootFolder: 'cypress/fixtures/' + baselinePath,
              actualPngRootFolder: 'cypress/downloads/compare-pdf/actual-png',
              baselinePngRootFolder: 'cypress/downloads/compare-pdf/baseline-png',
              diffPngRootFolder: 'cypress/downloads/compare-pdf/diff-png'
            },
            settings: {
              imageEngine: 'native',
              density: 100,
              quality: 70,
              tolerance: 0,
              threshold: 0.05,
              cleanPngPaths: false,
              matchPageCount: true
            }
          };

          // adding masks to hide the header on every page
          for (let i = 0; i < 50; i++) {
            masks.push({pageIndex: i, coordinates: {x0: 0, y0: 0, x1: 2000, y1: 60}});
          }

          return new comparePdf(config)
            .actualPdfFile(actualPdf)
            .baselinePdfFile(templatePdf)
            .addMasks(masks)
            .compare();
        }
      });
      return cloudPlugin(on, config);
    }
  },
})
