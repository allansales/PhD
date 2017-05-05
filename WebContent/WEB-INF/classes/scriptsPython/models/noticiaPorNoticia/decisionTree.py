from sklearn.metrics import confusion_matrix
from sklearn.externals.six import StringIO  
from sklearn import tree
import pydot 

def main():
 	CURR_PATH = path.dirname(path.realpath(__file__))
    dataset = pickle.load(open(CURR_PATH+'/arquivoTreino-Ibove3Classes.p', "rb" ) )
    train, target = dataset['train'], dataset['target']

def decisionTreeWindow(train,target):
    rf = tree.DecisionTreeClassifier(train,target)
    print("\tCrossValidation: %0.2f (+/- %0.2f)" % (scores.mean(), scores.std() * 2))

def matrizConfusao():
    #matriz confusao
    y_true = [0, 1, 2, 0, 1, 2, 0, 1, 2]
    y_pred = [0, 0, 0, 0, 1, 1, 0, 2, 2]
    confusion_matrix(y_true, y_pred)

def exibeArvoreGerada():
	dot_data = StringIO() 
	tree.export_graphviz(clf, out_file=dot_data) 
	graph = pydot.graph_from_dot_data(dot_data.getvalue()) 
	graph.write_pdf("iboveTree.pdf") 