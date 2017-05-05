rm(list = ls())

cols <- c("nome_empresa", "nome_pregao", "cod_negociacao", 
          "cod_cvm", "cnpj", "atividade_principal",  
          "setor", "sub_setor", "segmento", "site", "endereco", "cidade", "cep", 
          "estado", "telefone", "fax", "emails", "twitter_empresa", "facebook_empresa")
emp <- read.csv("data/DadosEmpresas.csv", header = F, col.names=cols, 
                colClasses = rep("character", length(cols)))

icon.dir <- "data/icons/"
icon.files <- list.files(icon.dir)
new.dir <- "data/icons_cnpj/"
dir.create(new.dir, showWarnings=F)

index.emps.with.icon <- c()
icon.names <- NULL

cat("Copying and Changing the icon filenames (based on empresa:cod_negociacao)...\n")
for(icon in icon.files){
  manager.code = unlist(strsplit(icon, "[.]"))[1]

  for(j in 1:length(emp$cod_negociacao)){
    manager.code.list = unlist(strsplit(as.character(emp$cod_negociacao[j]), ","))
    
    if(manager.code %in% manager.code.list){
      # Create a new icon name
      new.icon.name <- paste(emp$cnpj[j], ".jpg", sep ="")
      
      # Copy and change the name
      file.copy(paste(icon.dir, icon, sep = ""), paste(new.dir, icon, sep = ""), overwrite=T)
      file.rename(paste(new.dir, icon, sep = ""), paste(new.dir, new.icon.name, sep = ""))
      
      # Update the empresas with icons
      index.emps.with.icon <- c(index.emps.with.icon, j)
      
      # Update the icon names data.frame
      icon.names <- rbind(icon.names, data.frame(old.name=icon, new.name=new.icon.name))
    }
  }
}
cat("\n")

cat("Icon Names(Initial 10 only):\n")
print(head(icon.names, n = 10))
cat("\n")

cat("Empresas with icons (", length(index.emps.with.icon), "):\n")
print(emp[index.emps.with.icon, "nome_pregao"])
cat("\n")

cat("Empresas (nome_pregao) without icons(",  nrow(emp) - length(index.emps.with.icon), "):\n")
print(emp[-index.emps.with.icon, "nome_pregao"])
cat("\n")

cat("Empresas (cod_negociacao) without icons(",  nrow(emp) - length(index.emps.with.icon), "):\n")
print(emp[-index.emps.with.icon, "cod_negociacao"])
cat("\n")


icons.without.translation <- icon.files[!icon.files %in% icon.names$old.name]
cat("Icons without cod_negociacao (", length(icons.without.translation), "):\n")
print(icons.without.translation)
cat("\n")