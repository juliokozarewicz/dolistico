import { useTranslation } from 'react-i18next';

function App() {

    const { t } = useTranslation();

    return (

        <>

            <main id="center">

                <h1>{t('NO_PERMISSION_TO_ACCESS')}</h1>
                
            </main>

        </>

    )

}

export default App