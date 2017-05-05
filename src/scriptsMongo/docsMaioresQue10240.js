db.informacoesGerais.find(
    {$where:"this.conteudo.length > 10240"}
    ).forEach(
                function (doc){
                    print(doc._id +" "+doc.conteudo.length)
                })
