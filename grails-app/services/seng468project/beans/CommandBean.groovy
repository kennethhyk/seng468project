package seng468project.beans

import seng468project.enums.CommandEnum

/**
 * Created by kenneth on 2018-01-17.
 */
class CommandBean {
    CommandEnum command
    List<String> parameterList

    CommandBean(List<String> aCommand) {
        for(CommandEnum e : CommandEnum.values()){
            if(aCommand[0] == e as String){
                this.command = CommandEnum.valueOf(aCommand[0])
                break
            }
        }
        if(this.command == null) {
            this.command = null
        }
        if((aCommand.size()-1) == this.command.numberOfParameters) {
            this.parameterList = aCommand.subList(1,aCommand.size())
        } else {
            this.parameterList = null
        }
    }

    @Override
    public String toString() {
        return "CommandBean{" +
                "command=" + command +
                ", parameterList=" + parameterList +
                '}'
    }
}
