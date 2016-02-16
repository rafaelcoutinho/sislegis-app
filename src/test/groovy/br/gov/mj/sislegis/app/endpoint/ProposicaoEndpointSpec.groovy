package br.gov.mj.sislegis.app.endpoint

import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import spock.lang.Specification

class ProposicaoEndpointSpec extends Specification {

    def token = "Bearer eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiI4OTA0ZWEyNS04ZTZiLTQwZGItOGExNS05NzI5ODQ1NGRmNjMiLCJleHAiOjE0NTU2NTcyMjcsIm5iZiI6MCwiaWF0IjoxNDU1NjU2OTI3LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0L2F1dGgvcmVhbG1zL3Npc2xlZ2lzIiwiYXVkIjoic2lzbGVnaXMiLCJzdWIiOiI1ZWU3Y2U3Ni1lMjEwLTRlYjYtOTY1NS00MzE5ZWIyNjg2NjQiLCJhenAiOiJzaXNsZWdpcyIsInNlc3Npb25fc3RhdGUiOiI2YTM3NmVjOS1iZmNiLTQ5NjktYTVkYS1mY2ExM2Y0NzVkYWIiLCJjbGllbnRfc2Vzc2lvbiI6IjI2MDY2YWE2LTcwOTEtNDg4Mi1hZWQ0LTgwMWU5NWQyZmE1OSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vc2lzbGVnaXMubG9jYWwiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50Iiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6Ikd1c3Rhdm8gRGVsZ2FkbyIsInByZWZlcnJlZF91c2VybmFtZSI6Imd1c3Rhdm8iLCJnaXZlbl9uYW1lIjoiR3VzdGF2byIsImZhbWlseV9uYW1lIjoiRGVsZ2FkbyIsImVtYWlsIjoiZ2NkZWxnYWRvQGdtYWlsLmNvbSJ9.PJ_2owKkpH-0uhlerKJZBzM79KhamZqrVq7GQVFCVgLxz8r3H8xwUj24yxKKySUgZaJ3lymba9eoDs9RBO1JTghDOWftSLrl0e98l5idWSXvmHLseyFITf-FAfQ1qEFtaoMPfsvmce5pGkCRYemUOQK5Yba1B03594vZmONIUkloHGhRuCXFTnt_M8JFjpZsnwdcayfBr6tievjLFAVpxZS-l6KqM5dzperCnGeCgz_15gZLuGa58HRh5VVymkIx39j_LeBQzCxXLgc3qkjD48vVJbO_4A203vyt5TfDxcF454dUDK3ufQ8lwKJ8lbU4eN-Zs2W7mYfCbPL-8dicaA"
    def client = new RESTClient("http://localhost:8080/")
    def cabecalho = [Authorization: token]

    def "deve buscar proposicoes por pauta - Camara"() {
        given:
        def caminho = "/sislegis/rest/proposicaos/proposicoesPautaCamara/"
        def idComissao = 2001 //CAPADR
        def data = "10/20/2015"
        def query = [idComissao: idComissao, data: data]

        when:
        def resp = client.get(path: caminho, query: query, headers: cabecalho)

        then:
        resp.data.each {
            println it
        }
    }

    def "deve buscar proposicoes por pauta - Senado"() {
        given:
        def caminho = "/sislegis/rest/proposicaos/proposicoesPautaSenado/"
        def siglaComissao = "CAE"
        def data = "02/15/2016"
        def query = [siglaComissao: siglaComissao, data: data]

        when:
        def resp = client.get(path: caminho, query: query, headers: cabecalho)

        then:
        resp.data.each {
            println it
        }
    }

    def "deve trazer dados detalhados da proposicao - Camara"() {
        given:
        def caminho = "/sislegis/rest/proposicaos/detalharProposicaoCamaraWS/"
        def idProposicao = 1786728
        def query = [id: idProposicao]

        when:
        def resp = client.get(path: caminho, query: query, headers: cabecalho)

        then:
        resp.data.each {
            println it
        }
    }

    def "deve trazer dados detalhados da proposicao - Senado"() {
        given:
        def caminho = "/sislegis/rest/proposicaos/detalharProposicaoSenadoWS/"
        def id = 120529
        def query = [id: id]

        when:
        def resp = client.get(path: caminho, query: query, headers: cabecalho)

        then:
        resp.data.each {
            println it
        }
    }

    // TODO nao esta convertendo json
    def "deve salvar proposicoes de uma pauta"() {
        given:
        def caminho = "/sislegis/rest/proposicaos/salvarProposicoesDePauta"

        def dados = [
                pautaReunioes: [
                        id                : null,
                        comissao          : "CAE",
                        data              : "02/15/2016",
                        origem            : "SENADO",
                        codigoReuniao     : 4475,
                        linkPauta         : "http://legis.senado.leg.br/comissoes/reuniao?reuniao=4475",
                        titulo            : "CAE, \u00E0s 10h, 1\u00AA, Ordin\u00E1ria",
                        situacao          : "Agendada",
                        tipo              : "Ordin\u00E1ria",
                        manual            : false,
                        proposicoesDaPauta: [
                                proposicaoId          : null,
                                pautaReuniaoComissaoId: null,
                                ordemPauta            : 1,
                                relator               : "Senador Lindbergh Farias",
                                proposicao            : [
                                        ementa          : "Encaminha, nos termos do art. 6\u00BA da Lei n\u00BA 9.069, de 29 de junho de 1995, a Programa\u00E7\u00E3o Monet\u00E1ria para o 1\u00BA trimestre e para o ano de 2015, contendo estimativas das faixas de varia\u00E7\u00E3o dos principais agregados monet\u00E1rios, an\u00E1lise da evolu\u00E7\u00E3o da economia nacional e justificativa da programa\u00E7\u00E3o monet\u00E1ria.",
                                        tipo            : "MSF",
                                        numero          : "00011",
                                        ano             : "2015",
                                        sigla           : "MSF 00011/2015",
                                        comissao        : "CAE",
                                        idProposicao    : 120529,
                                        origem          : "SENADO",
                                        linkProposicao  : "http://www.senado.leg.br/atividade/materia/detalhes.asp?p_cod_mate=120529",
                                        ultimoComentario: [
                                                id         : 154,
                                                descricao  : "removida",
                                                autor      : [
                                                        id   : 1,
                                                        nome : "Gustavo Delgado",
                                                        email: "gcdelgado@gmail.com"
                                                ],
                                                dataCriacao: "02/15/2016",
                                                proposicao : null,
                                                oculto     : false
                                        ],
                                        reuniao         : [
                                                data: "02/15/2016"
                                        ]
                                ],
                                pautaReuniaoComissao  : [
                                        codigoReuniao: 4475
                                ],
                                resultado             : ""
                        ]
                ],
                reuniaoDate  : "02/15/2016"
        ]

        when:
        def resp = client.post(path: caminho, body: dados, headers: cabecalho, requestContentType: ContentType.JSON)

        then:
        assert resp.status == 200 // status 200 = Ok

    }

    def "deve salvar proposicao extra"() {
        given:

        def caminho = "/sislegis/rest/proposicaos/salvarProposicaoExtra"
        def dados = [
                id                           : null,
                idProposicao                 : 120529,
                tipo                         : "MSF",
                ano                          : "2015",
                numero                       : "00011",
                situacao                     : "INPAUTA",
                autor                        : "Presidente da República",
                origem                       : "SENADO",
                resultadoASPAR               : null,
                comissao                     : "SACAE",
                seqOrdemPauta                : null,
                sigla                        : "MSF 00011/2015",
                ementa                       : "Encaminha, nos termos do art. 6º da Lei nº 9.069, de 29 de junho de 1995, a Programação Monetária para o 1º trimestre e para o ano de 2015, contendo estimativas das faixas de variação dos principais agregados monetários, análise da evolução da economia nacional e justificativa da programação monetária.",
                linkProposicao               : "http://www.senado.leg.br/atividade/materia/detalhes.asp?p_cod_mate=120529",
                linkPauta                    : null,
                posicionamentoAtual          : null,
                posicionamentoPreliminar     : null,
                responsavel                  : null,
                tags                         : null,
                listaComentario              : [],
                listaEncaminhamentoProposicao: [],
                listaPautasComissao          : [],
                totalComentarios             : 0,
                totalEncaminhamentos         : 0,
                totalPautasComissao          : 0,
                proposicoesPai               : null,
                proposicoesFilha             : null,
                elaboracoesNormativas        : null,
                roadmapComissoes             : null,
                processosSei                 : null,
                favorita                     : false,
                pautaComissaoAtual           : null
        ]

        when:
        def resp = client.post(path: caminho, body: dados, headers: cabecalho, requestContentType: ContentType.JSON)

        then:
        assert resp.status == 200 // status 201 = Created
    }

    def "deve buscar a proposicao pelo id"() {
        given:
        def id = 150
        def caminho = "/sislegis/rest/proposicaos/" + id

        when:
        def resp = client.get(path: caminho, headers: cabecalho)

        then:
        resp.data.each {
            println it
        }
    }

    def "deve consultar proposicoes pelos filtros"() {
        given:
        def ementa = "com garantia da República Federativa do Brasil, entre o Estado do Rio Grande do Sul"
        def autor = ""
        def sigla = ""
        def origem = ""
        def isFavorita = ""
        def limit = "5"
        def offset = "0"
        def caminho = "/sislegis/rest/proposicaos/consultar"
        def query = [ementa: ementa, autor: autor, sigla: sigla, origem: origem, isFavorita: isFavorita, limit: limit, offset: offset]

        when:
        def resp = client.get(path: caminho, query: query, headers: cabecalho)

        then:
        resp.data.each {
            println it
        }
    }

    def "deve atualizar os dados de uma proposicao"() {
        given:
        def id = 165
        def caminho = "/sislegis/rest/proposicaos/" + id

        def dados = [id          : 165,
                     idProposicao: 115949,
                     tipo        : "MSF",
                     ano         : 2014,
                     numero      : 00001,
                     autor       : "Presidente da República",
                     comissao    : "ATA-PLEN",
                     situacao    : "TPRS",
                     origem      : "SENADO",
                     responsavel : [id   : 1,
                                    nome : "Gustavo Delgado",
                                    email: "gcdelgado@gmail.com"]
        ]

        when:
        def resp = client.put(path: caminho, body: dados, headers: cabecalho, requestContentType: ContentType.JSON)

        then:
        assert resp.status == 204 // status 204 = No Content
    }

    def "deve alterar o posicionamento de uma proposicao"() {

        given:
        def caminho = "/sislegis/rest/proposicaos/alterarPosicionamento"
        def id = 47
        def idPosicionamento = 7
        def preliminar = true
        def dados = [id: id, idPosicionamento: idPosicionamento, preliminar: preliminar]

        when:
        def resp = client.post(path: caminho, body: dados, headers: cabecalho, requestContentType: ContentType.JSON)

        then:
        assert resp.status == 200 // status 200 = Ok
    }

    def "deve listar o historico de alteracoes de posicionamento"() {

        given:
        def id = 35
        def caminho = "/sislegis/rest/proposicaos/historicoPosicionamentos/" + id

        when:
        def resp = client.get(path: caminho, headers: cabecalho)

        then:
        resp.data.each {
            println it
        }

    }

    def "deve atualizar o roadmap completo de comissoes de uma proposicao"() {

        given:
        def caminho = "/sislegis/rest/proposicaos/setRoadmapComissoes"
        def dados = [idProposicao: 29, comissoes: ['PLEN', 'CAPADR', 'CCJC']]

        when:
        def resp = client.post(path: caminho, body: dados, headers: cabecalho, requestContentType: ContentType.JSON)

        then:
        assert resp.status == 200 // status 200 = Ok
    }

}